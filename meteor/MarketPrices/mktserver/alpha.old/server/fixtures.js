Meteor.startup(function() {

    Products.remove({});

    Products.insert({
        name:       "IBM",
        type:       "STK",
        exchange:   "NYSE",
        currency:   "USD"
    });

});