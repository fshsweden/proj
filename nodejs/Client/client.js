/*  --------------------------------------------------------------------------


    --------------------------------------------------------------------------
*/
var http = require("http");
var querystring = require('querystring');

var data = JSON.stringify(
  {
    name: 'Peter Andersson',
    address: 'Mackmyra Byväg 36'
  }
);

var options = {
  host: 'localhost',
  port: 1337,
  path: '/price',   /* resource?id=foo&bar=baz', */
  method: 'POST',
  headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(data)
    }  
};

/*  --------------------------------------------------------------------------
    POST the data
    --------------------------------------------------------------------------
*/

var start = new Date().getTime();

for (var i = 0; i < 100000; i++) {
  var req = http.request(options, function(res) {
    /* console.log('STATUS: ' + res.statusCode);
    console.log('HEADERS: ' + JSON.stringify(res.headers)); */
    res.setEncoding('utf8');
    res.on('data', function (chunk) {
      /* console.log('BODY: ' + chunk); */
    });
  });

  req.write(data);
  req.end();
}

var end = new Date().getTime();
var time = end - start;

console.log("Execution time:" + time);
process.exit(1);

