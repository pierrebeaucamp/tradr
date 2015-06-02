'use strict';

// Include gulp & tools we'll use
var gulp = require('gulp');
var $ = require('gulp-load-plugins')();
var del = require('del');
var runSequence = require('run-sequence');
var pagespeed = require('psi');
var swPrecache = require('sw-precache');
var fs = require('fs');
var path = require('path');
var packageJson = require('./package.json');

// Optimize images
gulp.task('images', function () {
  return gulp.src('src/main/assets/images/**/*')
    .pipe($.cache($.imagemin({
      progressive: true,
      interlaced: true
    })))
    .pipe(gulp.dest('src/main/webapp/images'))
    .pipe($.size({title: 'images'}));
});

// Copy all files at the root level (src/main/assets)
gulp.task('copy', function () {
  return gulp.src([
    'src/main/assets/**'
  ], {
    dot: true
  }).pipe(gulp.dest('src/main/webapp'))
    .pipe($.size({title: 'copy'}));
});

// Copy web fonts to src/main/webapp
gulp.task('fonts', function () {
  return gulp.src(['src/main/assets/fonts/**'])
    .pipe(gulp.dest('src/main/webapp/fonts'))
    .pipe($.size({title: 'fonts'}));
});

// Compile and automatically prefix stylesheets
gulp.task('styles', function () {

  var AUTOPREFIXER_BROWSERS = [
    'ie >= 10',
    'ie_mob >= 10',
    'ff >= 30',
    'chrome >= 34',
    'safari >= 7',
    'opera >= 23',
    'ios >= 7',
    'android >= 4.4',
    'bb >= 10'
  ];

  // For best performance, don't add Sass partials to `gulp.src`
  return gulp.src([
    'src/main/assets/**/*.scss',
    'src/main/assets/styles/**/*.css'
  ])
    .pipe($.changed('.tmp/styles', {extension: '.css'}))
    .pipe($.sourcemaps.init())
    .pipe($.sass({
      precision: 10
    }).on('error', $.sass.logError))
    .pipe($.autoprefixer(AUTOPREFIXER_BROWSERS))
    .pipe(gulp.dest('.tmp'))
    // Concatenate and minify styles
    .pipe($.if('*.css', $.csso()))
    .pipe($.sourcemaps.write())
    .pipe(gulp.dest('src/main/webapp'))
    .pipe($.size({title: 'styles'}));
})

// Concatenate and minify JavaScript
gulp.task('scripts', function () {
  var sources = ['./src/main/assets/scripts/main.js'];
  return gulp.src(sources)
    .pipe($.concat('main.min.js'))
    .pipe($.uglify({preserveComments: 'some'}))
    // Output files
    .pipe(gulp.dest('src/main/webapp/scripts'))
    .pipe($.size({title: 'scripts'}));
});

// Scan your HTML for assets & optimize them
gulp.task('html', function () {
  var assets = $.useref.assets({searchPath: '{.tmp,src/main/assets}'});

  return gulp.src('src/main/twirl/**/*.html')
    .pipe(assets)
    // Remove any unused CSS
    .pipe($.if('*.css', $.uncss({
      html: [
        'src/main/twirl/**/*.html'
      ],
      // CSS Selectors for UnCSS to ignore
      ignore: [
        '/.navdrawer-container.open/',
        '/.src/main/assets-bar.open/'
      ]
    })))

    // Concatenate and minify styles
    // In case you are still using useref build blocks
    .pipe($.if('*.css', $.csso()))
    .pipe(assets.restore())
    .pipe($.useref())

    // Output files
    //.pipe(gulp.dest('src/main/webapp'))
    .pipe($.size({title: 'html'}));
});

// Clean output directory
gulp.task('clean', del.bind(null, ['.tmp', 'src/main/webapp/*', '!src/main/webapp/.git'], {dot: true}));

// Watch files for changes & reload
gulp.task('watch', ['styles'], function () {
  gulp.watch(['src/main/twirl/**/*.html']);
  gulp.watch(['src/main/assets/styles/**/*.{scss,css}'], ['styles']);
  gulp.watch(['src/main/assets/scripts/**/*.js'], ['jshint']);
  gulp.watch(['src/main/assets/images/**/*']);
});

// Build production files, the default task
gulp.task('default', ['clean'], function (cb) {
  runSequence(
    'styles',
    ['html', 'scripts', 'images', 'fonts', 'copy'],
    'generate-service-worker',
    cb);
});

// Run PageSpeed Insights
gulp.task('pagespeed', function (cb) {
  // Update the below URL to the public URL of your site
  pagespeed.output('tradr_seba.appspot.com', {
    strategy: 'mobile',
  }, cb);
});


// See http://www.html5rocks.com/en/tutorials/service-worker/introduction/ for
// an in-depth explanation of what service workers are and why you should care.
// Generate a service worker file that will provide offline functionality for
// local resources. This should only be done for the 'src/main/webapp' directory, to allow
// live reload to work as expected when serving from the 'src/main/assets' directory.
gulp.task('generate-service-worker', function (callback) {
  var rootDir = 'src/main/webapp';
  var tmpltDir = 'src/main/twirl';

  swPrecache({
    // Used to avoid cache conflicts when serving on localhost.
    cacheId: packageJson.name || 'tradr',
    // URLs that don't directly map to single static files can be defined here.
    // If any of the files a URL depends on changes, then the URL's cache entry
    // is invalidated and it will be refetched.
    // Generally, URLs that depend on multiple files (such as layout templates)
    // should list all the files; a change in any will invalidate the cache.
    // In this case, './' is the top-level relative URL, and its response
    // depends on the contents of the files 'src/main/twirl/index.scala.html' and
    // 'src/main/twirl/main.scala.html'
    dynamicUrlToDependencies: {
      './': [
        path.join(tmpltDir, '/index.scala.html'),
        path.join(tmpltDir, '/main.scala.html')      
      ]
    },
    staticFileGlobs: [
      // Add/remove glob patterns to match your directory setup.
      rootDir + '/fonts/**/*.woff',
      rootDir + '/images/**/*',
      rootDir + '/scripts/**/*.js',
      rootDir + '/styles/**/*.css',
      rootDir + '/*.{html,json}'
    ],
    // Translates a static file path to the relative URL that it's served from.
    stripPrefix: path.join(rootDir, path.sep)
  }, function (error, serviceWorkerFileContents) {
    if (error) {
      return callback(error);
    }
    fs.writeFile(path.join(rootDir, 'service-worker.js'),
      serviceWorkerFileContents, function (error) {
      if (error) {
        return callback(error);
      }
      callback();
    });
  });
});
