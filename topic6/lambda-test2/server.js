var ApiBuilder = require('claudia-api-builder'),
	api = new ApiBuilder();

module.exports = api;

api.get('/hello', function(){
	return '475 software engineering for industry is so good';
});
