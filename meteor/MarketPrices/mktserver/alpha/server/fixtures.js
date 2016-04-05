function init(name) {
    Prices.insert({
        name: name,
        bidqty: 0,
        bid: 0,
        ask: 0,
        askqty: 0,
        last: 0,
        lastqty: 0,
        volume: 0,
        update_time: 0
    });
}

Meteor.startup(function () {
    Prices.remove({});

    init("ERIC.B");
     /* init("ABB"); */

});