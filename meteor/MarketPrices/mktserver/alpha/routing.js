Router.route('/', function () {
    this.layout('ApplicationLayout');
    this.render('left', {to: 'Left'});
    this.render('home', {to: 'Main'});
});

