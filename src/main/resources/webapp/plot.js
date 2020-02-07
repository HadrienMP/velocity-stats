// ########################################

// Finished tickets by month

// ########################################

$.get("/stats/tickets-finished-per-month", function (stats) {
    Plotly.newPlot(
        'monthPlot',
        toPlotData(stats),
        { barmode: 'stack', title: 'Number of tickets done by month' });
    Plotly.newPlot(
        'monthBoxPlot',
        toBoxPlotData(stats),
        { title: 'Boxplot of the number of tickets done by month' });
});

$.get("/stats/tickets-finished-per-week", function (stats) {
    Plotly.newPlot(
        'weekPlot',
        toPlotData(stats),
        { barmode: 'stack', title: 'Number of tickets done by week' });

    Plotly.newPlot(
        'weekBoxPlot',
        toBoxPlotData(stats),
        { title: 'Boxplot of the number of tickets done by week' });
});

$.get("/stats/cycle-times", function (stats) {
    Plotly.newPlot(
        'cycleTimes',
        [
            boxplot(stats["0"], '0 points'),
            boxplot(stats["1"], '1 points'),
            boxplot(stats["2"], '2 points'),
            boxplot(stats["3"], '3 points'),
            boxplot(stats["5"], '5 points')
        ],
        { title: 'Cycle times / story points' });
});

$.get("/stats/points-repartition", function (stats) {
    var data = [{
        values: Object.values(stats),
        labels: Object.keys(stats),
        type: 'pie'
    }];

    var layout = {
        height: 400,
        width: 500,
        title: "Repartition of points"
    };

    Plotly.newPlot('pointsRepartition', data, layout);
});

//

// ########################################

// Plot functions

// ########################################

function toPlotData(stats) {
    var features = {
        x: Object.keys(stats["stories"]),
        y: Object.values(stats["stories"]),
        name: 'Stories',
        type: 'bar',
        marker: {
            color: '#95cf95',
            line: {
                color: '#2ca02c',
                width: 2
            }
        }
    };

    var others = {
        x: Object.keys(stats["unknowns"]),
        y: Object.values(stats["unknowns"]),
        name: 'Other',
        type: 'bar',
        marker: {
            color: '#8ebad9',
            line: {
                color: '#1f77b4',
                width: 2
            }
        }
    };

    var bugs = {
        x: Object.keys(stats["bugs"]),
        y: Object.values(stats["bugs"]),
        name: 'Bugs',
        type: 'bar',
        marker: {
            color: '#ea9293',
            line: {
                color: '#d62728',
                width: 2
            }
        }
    };
    var points = plot(stats["points"], 'Points');
    points['marker'] = {color: '#000'};

    return [features, others, bugs, points];
}

function toBoxPlotData(stats) {
    return [
        boxplot(Object.values(stats["stories"]), 'Stories'),
        boxplot(Object.values(stats["points"]), 'Points'),
        boxplot(Object.values(stats["bugs"]), 'Bugs'),
        boxplot(Object.values(stats["tickets"]), 'Tickets')
    ];
}

function plot(dict, name) {

    var keys = Object.keys(dict).sort();

    var values = [];
    keys.forEach(function (key) {
        values.push(dict[key]);
    });

    return {
        x: keys,
        y: values,
        type: 'scatter',
        name: name
    };
}

function boxplot(values, name) {
    return {
        y: values,
        type: 'box',
        boxmean: 'sd',
        name: name,
        boxpoints: 'suspectedoutliers'
    };
}