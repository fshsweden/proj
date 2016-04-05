Template.home.helpers({
    prices: Prices.find({})
});

Template.home.onRendered(function() {
    console.log("--- rendered ----");
    var query = Prices.find({});

    var handle = query.observeChanges({
        added: function(id, fields) {

        },
        changed: function (id, fields) {

            // console.log("id is a " + (typeof id) + " and type of fields is " + (typeof fields));

            for (var field in fields) {
                
                switch (field) {
                    case 'bidqty':
                        // hightlight foreground
                        jQuery('#' + id + "-bidqty").animate({
                            color: "#ff0000"
                        }, 400).animate({color: "#000000"}, 600);
                    break;
                    case 'bid':
                        //this will highlight the row of the updated favorite, using the id from the Favorites collection
                        // $('#' + id + "-bid").effect('highlight', {color:'red'}, 500);

                        // hightlight background
                        // $('#' + id + "-ask").effect('highlight', {color:'blue'}, 500);

                        // hightlight foreground

                        if (fields['bidchg'] === "-") {
                            $('#' + id + "-bid").effect('highlight', {color:'red'}, 500);
                        }
                        else if (fields['bidchg'] === "+") {
                            $('#' + id + "-bid").effect('highlight', {color:'blue'}, 500);
                        }
                        else {
                            $('#' + id + "-bid").effect('highlight', {color:'white'}, 500);
                        }

                        /* jQuery('#' + id + "-bid").animate({
                            color: mycolor
                        }, 400).animate({color: "#000000"}, 600); */
                    break;

                    case 'ask':
                        if (fields['askchg'] === "-") {
                            $('#' + id + "-ask").effect('highlight', {color:'red'}, 500);
                        }
                        else if (fields['askchg'] === "+") {
                            $('#' + id + "-ask").effect('highlight', {color:'blue'}, 500);
                        }
                        else {
                            $('#' + id + "-ask").effect('highlight', {color:'white'}, 500);
                        }

                        // hightlight foreground
                        /* jQuery('#' + id + "-ask").animate({
                            color: mycolor
                        }, 400).animate({color: "#000000"}, 600); */
                    break;
                    case 'askqty':
                        // hightlight foreground
                        jQuery('#' + id + "-askqty").animate({
                            color: "#ff0000"
                        }, 400).animate({color: "#000000"}, 600);
                    break;
                }
            }

        },
        removed: function() {
            /* which document was removed? */
        }
    });
});
