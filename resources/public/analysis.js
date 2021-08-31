window.chartOptions = {};
window.chartFormulas = {};

if( document.readyState !== 'loading' ) {
    runCharts();
} else {
    document.addEventListener('DOMContentLoaded', function () {
        runCharts();
    });
}

function runCharts() {
    document.getElementById("chart-type").querySelector("select").addEventListener("change", (e) => {
        //clear toggles
        window.scatter = null;
        window.line = null;
        var val = e.target.value;
        switch (val) {
            case 'stat-analysis':  {
                statAnalysisChart('all');
                addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all') },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high')},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med')},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low')}
                ]);
            } break;
            case 'total-analysis': {
                totalAnalysisChart('all');
                addOptions([
                    {key: 'All', value: 'all', cb: () => totalAnalysisChart('all') },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => totalAnalysisChart('high')},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => totalAnalysisChart('med')},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => totalAnalysisChart('low')}
                ]);
            } break;
            case 'fire-analysis': {
                var type = 'Fire'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
            case 'water-analysis': {
                var type = 'Water'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
            case 'earth-analysis': {
                var type = 'Earth'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
            case 'wind-analysis': {
                var type = 'Wind'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
            case 'light-analysis': {
                var type = 'Light'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
            case 'dark-analysis': {
                var type = 'Dark'
                statAnalysisChart('all', type);
                 addOptions([
                    {key: 'All', value: 'all', cb: () => statAnalysisChart('all', type) },
                    {key: 'Stats 600+', value: 'stats-high', cb: () => statAnalysisChart('high', type)},
                    {key: 'Stats 450-599', value: 'stats-med', cb: () => statAnalysisChart('med', type)},
                    {key: 'Stats Less than 450', value: 'stats-low', cb: () => statAnalysisChart('low', type)}
                ]);
            } break;
        }
    });
    document.getElementById("chart-options").querySelector("select").addEventListener("change", (e) => {
        //clear toggles
        window.scatter = null;
        window.line = null;
        var val = e.target.value;
        window.chartOptions[val].call();
    });
    multiLinearRegression('/stats-regression');
}

function addOptions(options) {
    window.chartOptions = {};
    var target = document.getElementById("chart-options")
      .querySelector("select");
      target.options.length = 0;
    options.forEach(item => {
        var option = document.createElement("option");
        option.innerText = item.key;
        option.value = item.value;
        window.chartOptions[option.value] = item.cb;
        target.options.add(option);
    });
    target.selectedIndex = 0;
}



function statToScatter(characters, x, y, label, backgroundColor) {
    return {
              type: 'scatter',
              label: label,
              data: characters.map(character => {
                  return {x: Number(character[x]), y: character[y]}
              }),
               backgroundColor: backgroundColor
          }
}

function statToBubble(characters, x1, x2, y, ratio, label, backgroundColor) {
    return {
              type: 'bubble',
              label: label,
              data: characters.map(character => {
                  return {x: character[x1], y: character[y], r: character[x2] / ratio}
              }),
               backgroundColor: backgroundColor
          }
}

function statLinearRegression(characters, stat, label, color, min, max) {
    var stats = characters.map(character =>  [Number(character[stat]), character.winrate]);
    var calc = regression.linear(stats);
    var start = calc.predict(min);
    var end = calc.predict(max);
    window.chartFormulas[stat] = { label: label, equation: calc.equation };
    return {
            type: 'line',
            label: label,
            formula: 'y = ' + calc.equation[0].toFixed(2) + 'x + ' + calc.equation[1].toFixed(2),
//            formula: calc.string,
            r2: (calc.r2 * 100).toFixed(2),
            data: [{x: start[0], y: start[1]}, {x: end[0], y: end[1]}],
            backgroundColor: color,
            borderColor: color,
            pointBackgroundColor:'rgba(0, 0, 0, 0.0)',
            pointBorderColor:'rgba(0, 0, 0, 0.0)'
        }
}

function statMultiLinearRegression(characters, stats, label, color) {
    var calc =
    regression.linear(
        stats.flatMap(stat =>
            {
               var calc = regression.linear(characters.map(
                    character => [Number(character[stat]), character.winrate]
               ));
               results = [];
               results.push(calc.predict(35));
               results.push(calc.predict(60));
               results.push(calc.predict(100));
               results.push(calc.predict(150));
               return results;
            }
        )
    );
    var start = calc.predict(35);
    var end = calc.predict(150);
    window.chartFormulas['multi'] = { label: label, equation: calc.equation };
    return {
                type: 'line',
                label: label,
                formula: 'y = ' + calc.equation[0].toFixed(2) + 'x + ' + calc.equation[1].toFixed(2),
                r2: (100 - calc.r2 * 100).toFixed(2),
                data: [{x: start[0], y: start[1]}, {x: end[0], y: end[1]}],
                backgroundColor: color,
                borderColor: color,
                pointBackgroundColor:'rgba(0, 0, 0, 0.0)',
                pointBorderColor:'rgba(0, 0, 0, 0.0)'
            }
}

function multiLinearRegression(url) {
     fetch(window.location.href + url)
            .then(response => response.json())
            .then(data => {
                window.chartData = JSON.parse(data);
                window.analysis = new Chart(
                    document.getElementById("regression"),
                    {
                        type: 'scatter',
                        data: {
                            labels: "Analysis, ",
                            datasets: []
                        },
                        options: {
                            devicePixelRatio: 2.4,
                            plugins: {
                                    tooltip: {
                                        enabled: false
                                    },
                                    legend: {
                                        position: 'top'
                                    }
                           }
                        }
                    }
                )
                document.getElementById("chart-type").querySelector("select").value = 'stat-analysis';
                var e = new Event("change");
                var element = document.getElementById("chart-type").querySelector("select");
                element.dispatchEvent(e);
            });
}

function statAnalysisChart(dataType) {
    statAnalysisChart(dataType, null);
}
function statAnalysisChart(dataType, type) {
    var data;
    switch (dataType) {
        case 'all': data = window.chartData; break;
        case 'high': data = window.chartData.filter(character => character.total_stats >= 600); break;
        case 'med': data = window.chartData.filter(character => character.total_stats >= 450 && character.total_stats < 600); break;
        case 'low': data = window.chartData.filter(character => character.total_stats >= 0 && character.total_stats < 450); break;
    }
    if (type != null) {
        data = data.filter(character => character.type === type);
    }
    window.analysis.data.datasets = [];
    window.analysis.data.datasets.push(statToScatter(data, 'hp', 'winrate', 'HP',               'rgba(222,    4,   5,  0.2)'));
    window.analysis.data.datasets.push(statToScatter(data, 'phy_attack', 'winrate', 'Phy Atk',  'rgba(244,    152,    4,  0.2)'));
    window.analysis.data.datasets.push(statToScatter(data, 'mag_attack', 'winrate', 'Mag Atk',  'rgba( 250 ,    205,  16,  0.2)'));
    window.analysis.data.datasets.push(statToScatter(data, 'phy_defense', 'winrate', 'Phy Def', 'rgba(12,    181,  53,  0.2)'));
    window.analysis.data.datasets.push(statToScatter(data, 'mag_defense', 'winrate', 'Mag Def', 'rgba(44,   103,   246,  0.2)'));
    window.analysis.data.datasets.push(statToScatter(data, 'speed', 'winrate', 'Speed',         'rgba( 91,   35,   213,  0.2)'));

    window.analysis.data.datasets.push(statLinearRegression(data, 'hp', 'HP Line',               'rgba(222,    4,   5,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statLinearRegression(data, 'phy_attack', 'Phy Atk Line',  'rgba(244,    152,    4,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statLinearRegression(data, 'mag_attack', 'Mag Atk Line',  'rgba(  250,    205,  16,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statLinearRegression(data, 'phy_defense', 'Phy Def Line', 'rgba(12,    181,  53,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statLinearRegression(data, 'mag_defense', 'Mag Def Line', 'rgba(44,   103,   246,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statLinearRegression(data, 'speed', 'Speed Line',         'rgba( 91,   35,   213,  1.0)', 35, 150));
    window.analysis.data.datasets.push(statMultiLinearRegression(data, ['hp', 'phy_attack', 'mag_attack', 'phy_defense', 'mag_defense', 'speed'], 'Mutli-Regression',         'rgba( 0,   0,   0,  1.0)'))

    window.analysis.update();
    var target = document.getElementById("coefficients");
    target.classList.add("border");
    target.innerHTML = null;
    window.analysis.data.datasets.filter(line => line.type === 'line').forEach((line, i) => {
        var legend = target.children[target.children.length - 1];
        if (legend === undefined || legend.children.length % 3 == 0) {
            legend = document.createElement("div");
            legend.classList.add("row");
            target.appendChild(legend);
        }
        var div = document.createElement("div");
        div.classList.add("col");
        div.classList.add("p-2");
        div.classList.add("d-flex");
        div.classList.add("justify-content-center");
        div.innerHTML = '<b><span style="color:' + line.backgroundColor + ';">' + line.label + ':</span><br/>R<sup>2</sup>:&nbsp;&nbsp;&nbsp;&nbsp;' + line.r2 + '%<br/>Line: '+ line.formula + '</b>';
        legend.appendChild(div);
    });

    var legend = document.createElement("div");
    legend.classList.add("row");

    var div = document.createElement("div");
    div.classList.add("col");
    div.classList.add("p-2");
    div.classList.add("d-flex");
    div.classList.add("justify-content-between");

    var selectContainer = createContainer('Prediction Equation','me-2');
    var select = document.createElement("select");
    select.classList.add("form-select");
    select.id = "predict-stat";
    Object.keys(window.chartFormulas).forEach(stat => {
            if (stat !== 'multi') {
                var option = document.createElement("option");
                option.innerText = window.chartFormulas[stat].label;
                option.value = stat;
                select.options.add(option);
            }
    });
    selectContainer.appendChild(select);
    div.appendChild(selectContainer);

    var inputContainer = createContainer('Predict', 'me-2');
    var input = document.createElement('input');
    input.classList.add("form-control");
    input.id = "predict-value";
    inputContainer.appendChild(input);
    div.appendChild(inputContainer);

    var resultContainer = createContainer('Result', 'fw-bold');
    var result = document.createElement('div');
    result.classList.add("mt-2");
    result.id = "predict-result";
    result.style.minWidth = "150px";
    resultContainer.appendChild(result);
    div.appendChild(resultContainer);

    legend.appendChild(div);
    target.appendChild(legend);

    select.addEventListener('change', (e) => {
        var stat = e.target.value;
        var predictFor = document.getElementById('predict-value').value;
        predictWinRate(stat, predictFor);
    });

    input.addEventListener('keyup', (e) => {
            var stat = document.getElementById('predict-stat').value;
            var predictFor = e.target.value;
            predictWinRate(stat, predictFor);
    });
}

function predictWinRate(stat, predictFor) {
    var base = window.chartFormulas[stat].equation[0] * predictFor + window.chartFormulas[stat].equation[1];
    var multi = window.chartFormulas['multi'].equation[0] * predictFor + window.chartFormulas['multi'].equation[1];
    var results = [{ stat: window.chartFormulas[stat].label, value: base }, { stat: 'Multi', value: multi }];
    results.sort((a, b) => a.value > b.value);
    document.getElementById('predict-result').innerText = results[0].stat + ': ' + results[0].value.toFixed(2) + '% to ' + results[1].stat + ': ' + results[1].value.toFixed(2) + '%';
}

function createContainer(labelText, cssClass) {
    var div = document.createElement("div");
    div.classList.add(cssClass);
    var label = document.createElement("label");
    label.classList.add('fw-bold');
    label.innerText = labelText;
    div.appendChild(label)
    return div;
}
function totalAnalysisChart(dataType) {
    var data;
    switch (dataType) {
        case 'all': data = window.chartData; break;
        case 'high': data = window.chartData.filter(character => character.total_stats >= 600); break;
        case 'med': data = window.chartData.filter(character => character.total_stats >= 450 && character.total_stats < 600); break;
        case 'low': data = window.chartData.filter(character => character.total_stats >= 0 && character.total_stats < 450); break;
    }

    var target = document.getElementById("coefficients");
    target.innerHTML = null;
    var smallest = data.reduce((current, next) => current.total_stats < next.total_stats ? current : next);
    var largest = data.reduce((current, next) => current.total_stats > next.total_stats ? current : next);
    window.analysis.data.datasets = [];
    window.analysis.data.datasets.push(statToScatter(data, 'total_stats', 'winrate', 'Total Stats',  'rgba(244,    67,   54,  0.4)'));
    window.analysis.data.datasets.push(statLinearRegression(data, 'total_stats', 'Total Stats Line', 'rgba(244,    67,   54,  1.0)', smallest.total_stats, largest.total_stats));
    window.analysis.update()

    var target = document.getElementById("coefficients");
    target.classList.add("border");
    target.innerHTML = null;
    window.analysis.data.datasets.filter(line => line.type === 'line').forEach((line, i) => {
        var legend = target.children[target.children.length - 1];
        if (legend === undefined || legend.children.length % 3 == 0) {
            legend = document.createElement("div");
            legend.classList.add("row");
            target.appendChild(legend);
        }
        var div = document.createElement("div");
        div.classList.add("col");
        div.classList.add("p-2");
        div.classList.add("d-flex");
        div.classList.add("justify-content-center");
        div.innerHTML = '<b><span style="color:' + line.backgroundColor + ';">' + line.label + ':</span><br/>R<sup>2</sup>:&nbsp;&nbsp;&nbsp;&nbsp;' + line.r2 + '%<br/>Line: '+ line.formula + '</b>';
        legend.appendChild(div);
    });
}

function toggleAllScatters() {
    if (window.scatter == null) {
        window.scatter = window.analysis.data.datasets.filter(dataset => dataset.type === 'scatter');
        window.analysis.data.datasets = window.analysis.data.datasets.filter(dataset => dataset.type !== 'scatter');
    } else {
        window.scatter.forEach(scatter => window.analysis.data.datasets.push(scatter));
        window.scatter = null;
    }
    window.analysis.update();
}
function toggleAllLines(){
    if (window.line == null) {
        window.line = window.analysis.data.datasets.filter(dataset => dataset.type === 'line');
        window.analysis.data.datasets = window.analysis.data.datasets.filter(dataset => dataset.type !== 'line');
    } else {
        window.line.forEach(line => window.analysis.data.datasets.push(line));
        window.line = null;
    }
    window.analysis.update();
}
