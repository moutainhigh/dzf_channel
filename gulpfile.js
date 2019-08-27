var gulp = require('gulp');
var uglify = require('gulp-uglify');   ////用于压缩js文件
var minifyCSS = require('gulp-minify-css');   ////用于压缩css文件
var babel = require('gulp-babel');

/////用于压缩js文件
gulp.task('script',function(){
    /////找到需要压缩的文件
    gulp.src('Embedded/webapps/dzf_channel/js/**/*.js')   ////  /**/  表示js目录下的任意层级的目录
    //////把ES6代码转成ES5代码
    .pipe(babel())
    /////压缩文件
    .pipe(uglify())
    /////另存压缩后文件
    .pipe(gulp.dest('Embedded/webapps/dzf_channel/js'));
});

gulp.task('css',function(){
    gulp.src('Embedded/webapps/dzf_channel/css/**/*.css')
    .pipe(minifyCSS())
    .pipe(gulp.dest('Embedded/webapps/dzf_channel/css'));
});

gulp.task('default',['script']);