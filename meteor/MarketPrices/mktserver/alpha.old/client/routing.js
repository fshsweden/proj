Router.route('/', function () {
    this.layout('ApplicationLayout');
    this.render('Left', {to: 'left'});
    this.render('Main', {to: 'main'});
});

Router.route('/page1', function () {
    this.render('Page1');
});

