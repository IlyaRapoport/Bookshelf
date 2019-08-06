
function diagram(label, y) {

//Better to construct options first and then pass it as a parameter
var options = {
	title: {
		text: "Statistic of books what was downloaded"
	},
	data: [
	{
		// Change type to "doughnut", "line", "splineArea", etc.
		type: "column",
		dataPoints: [

{ label:"asdas",  y:11  }
		]
	}
	]
};

$("#chartContainer").CanvasJSChart(options);

$("#addDataPoint").ready(function () {
		var chart = $("#chartContainer").CanvasJSChart();
		chart.options.data[0].dataPoints.push({ label:"weqw",  y:22 });
		chart.render();

	});

}

