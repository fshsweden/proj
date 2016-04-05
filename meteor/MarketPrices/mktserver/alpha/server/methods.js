Meteor.startup(function() {

});

Meteor.methods({
    "updateProduct" : function(options) {

        // console.log("updateProduct called with:" + JSON.stringify(options));

        var ret = {};
        var ret = JSON.parse(options);

        /*
        options.replace(/(\b[^:]+):'([^']+)'/g, function ($0, param, value)      {
            console.log("Setting " + param + " to " + value);
            ret[param] = value;
        });
        */

        console.log("Updating record # " + ret['name'] + " with " + JSON.stringify(ret));

        var old = Prices.findOne({'name' : ret['name']});
        if (old == undefined) {
            ret['bidchg'] = '+';
            ret['askchg'] = '+';
        }
        else {

            var oldbid = parseFloat(old['bid']);
            var oldask = parseFloat(old['ask']);

            var bid = parseFloat(ret['bid']);
            var ask = parseFloat(ret['ask']);

            console.log("Old value was " + old['bid']);
            if (bid > oldbid) {
                console.log("*********** INCREASING BID *************");
                ret['bidchg'] = '+';
            }
            else
                if (bid < oldbid) {
                    console.log("******** DECREASING BID **************");
                    ret['bidchg'] = '-';
                }
                else {
                    ret['bidchg'] = ' ';
                }

            if (ask > oldask)
                ret['askchg'] = '+';
            else
                if (ask < oldask)
                    ret['askchg'] = '-';
                else
                    ret['askchg'] = ' ';
        }

        // Prices.update({name: ret['name']},  ret);
        Prices.upsert({name: ret['name']},  ret);
    }
});
