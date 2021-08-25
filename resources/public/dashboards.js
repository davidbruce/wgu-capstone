if( document.readyState !== 'loading' ) {
    runCharts();
} else {
    document.addEventListener('DOMContentLoaded', function () {
        runCharts();
    });
}

function runCharts() {
    winRateByType('/character-win-rate-by-type', 'character-win-rate-by-type', "Win Rate By Character Type");
    winRateByType('/action-win-rate-by-type', 'action-win-rate-by-type', "Win Rate By Action Type");
    winRateCluster('/character-win-rate', 'character-win-rate', "Character Win Rates");
    winRateCluster('/action-win-rate', 'action-win-rate', "Action Win Rates");
    multiLinearRegression('/stats-regression');
}

function winRateByType(url, id, text) {
    fetch(window.location.href + url)
        .then(response => response.json())
        .then(data => {
            var types = JSON.parse(data);

            var data = {
              labels: types.flatMap(type => type.name),
              datasets: [
                {
                  label: 'Dataset 1',
                  data: types.flatMap(type => type.winrate),
                  backgroundColor: [
                    'rgba(244, 67, 54,   0.8)',
                    'rgba(3, 169, 244,   0.8)',
                    'rgba(46, 125, 50,   0.8)',
                    'rgba(103, 58, 183,  0.8)',
                    'rgba(255, 255, 255, 0.8)',
                    'rgba(33, 33, 33,    0.8)'
                  ],
                  borderColor: [
                    'rgba(99, 99, 99,    1.0)',
                    'rgba(99, 99, 99,    1.0)',
                    'rgba(99, 99, 99,    1.0)',
                    'rgba(99, 99, 99,    1.0)',
                    'rgba(99, 99, 99,    1.0)',
                    'rgba(99, 99, 99,    1.0)'
                  ]
                }
              ]
            };

            var config = {
              type: 'polarArea',
              data: data,
              options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                  legend: {
                    position: 'top',
                  },
                  title: {
                    display: true,
                    text: text
                  }
                }
              },
            };
            new Chart(
                document.getElementById(id),
                config
            );
        }
    );
}
function getColor(type) {
     switch (type) {
        case 'Fire':  return 'rgba(244, 67, 54,   0.8)';
        case 'Water': return 'rgba(3, 169, 244,   0.8)';
        case 'Earth': return 'rgba(46, 125, 50,   0.8)';
        case 'Wind':  return 'rgba(103, 58, 183,  0.8)';
        case 'Light': return 'rgba(255, 255, 255, 0.8)';
        case 'Dark':  return 'rgba(33, 33, 33,    0.8)';
    }
}
function winRateCluster(url, id, text) {
    fetch(window.location.href + url)
        .then(response => response.json())
        .then(data => {
            var types = JSON.parse(data);

            var data = {
              labels: Object.keys(types),
              datasets:
                    Object.keys(types).map(type =>  {
                      return {
                            label: type,
                            raw: types[type],
                            data: types[type].flatMap(result => { return { x: result.winrate, y: result.games } }),
                            backgroundColor: getColor(type),
                            borderColor: 'rgba(133, 133, 133,    0.8)'

                        }
                    })
            };

            var config = {
              type: 'scatter',
              data: data,
              options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                  legend: {
                    position: 'top',
                  },
                  title: {
                    display: true,
                    text: text
                  },
                  tooltip: {
                                         callbacks: {
                                            title: function(tooltipItemArray) {
                                               var item = tooltipItemArray[0].dataset.raw[tooltipItemArray[0].dataIndex];
                                               return item.target;
                                            },
                                            label: function(tooltipItem) {
                                               var item = tooltipItem.dataset.raw[tooltipItem.dataIndex];
                                               return  ': ' + item.winrate + '%';
                                            },
                                            footer: function(tooltipItemArray) {
                                               var item = tooltipItemArray[0].dataset.raw[tooltipItemArray[0].dataIndex];
                                               return "Games: " + item.games + "\nValues ID: " + item.values_id;
                                            },

                                         }
                                      }
                }
              },
            };
            new Chart(
                document.getElementById(id),
                config
            );
        }
    );
}

function statToScatter(characters, x, y, label, backgroundColor) {
    return {
              type: 'scatter',
              label: label,
              data: characters.map(character => {
                  return {x: character[x], y: character[y]}
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

function statLinearRegression(characters, stat, label, color) {
    var stats = characters.map(character =>  [Number(character[stat]), character.winrate]);
    var calc = regression.linear(stats);
    console.log(stat);
    console.log(calc);
    return {
            type: 'line',
            label: label,
            data:  calc.points.map(point => { return {x: point[0], y: point[1]}}),
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
                var characters = JSON.parse(data);

                new Chart(
                    document.getElementById("regression"),
                    {
                        type: 'scatter',
                        data: {
                            labels: "Test",
                            datasets: [
                                statToBubble(characters, 'total_stats', 'phy_attack', 'winrate', 10, 'HP',               'rgba(244,    67,   54,  0.4)'),
                                statToBubble(characters, 'total_stats', 'phy_defense', 'winrate', 10, 'HP',               'rgba(122,    67,  122,  0.4)'),
//                                statToBubble(characters, 'phy_attack', 'total_stats', 'winrate', 100, 'HP',               'rgba(244,    67,   54,  0.4)'),
//                                statToScatter(characters, 'total_stats', 'Stats',               'rgba(244,    67,   54,  0.4)'),
//                                statLinearRegression(characters, 'total_stats', 'Stats',               'rgba(244,    67,   54,  0.8)'),
//                                statToScatter(characters, 'offense', 'offense',               'rgba(244,    67,   54,  0.4)'),
//                                statLinearRegression(characters, 'offense', 'offense',               'rgba(244,    67,   54,  0.8)'),
//                                statToScatter(characters, 'defense', 'defense',               'rgba(244,    67,   54,  0.4)'),
//                                statLinearRegression(characters, 'defense', 'defense',               'rgba(244,    67,   54,  0.8)'),
//                                statLinearRegression(characters, 'hp', 'HP',               'rgba(244,    67,   54,  0.8)'),

//                                statToScatter(characters, 'hp', 'winrate', 'HP',               'rgba(244,    67,   54,  0.4)'),
//                                statLinearRegression(characters, 'hp', 'HP',               'rgba(244,    67,   54,  0.8)'),
//
//                                statToScatter(characters, 'phy_attack', 'winrate', 'Phy Atk',  'rgba(244,    67,    0,  0.4)'),
//                                statLinearRegression(characters, 'phy_attack', 'Phy Atk',  'rgba(244,    67,    0,  0.8)'),
//
//                                statToScatter(characters, 'mag_attack', 'winrate', 'Mag Atk',  'rgba(  0,    67,  244,  0.4)'),
//                                statLinearRegression(characters, 'mag_attack', 'Mag Atk',  'rgba(  0,    67,  244,  0.8)'),
//
//                                statToScatter(characters, 'phy_defense', 'winrate', 'Phy Def', 'rgba(122,    67,  122,  0.4)'),
//                                statLinearRegression(characters, 'phy_defense', 'Phy Def', 'rgba(122,    67,  122,  0.8)'),
//
//                                statToScatter(characters, 'mag_defense', 'winrate', 'Mag Def', 'rgba(122,   122,   54,  0.4)'),
//                                statLinearRegression(characters, 'mag_defense', 'Mag Def', 'rgba(122,   122,   54,  0.8)'),
//
//                                statToScatter(characters, 'speed', 'winrate', 'Speed',         'rgba( 54,   244,   67,  0.4)'),
//                                statLinearRegression(characters, 'speed', 'Speed',         'rgba( 54,   244,   67,  0.8)'),

                            ]
                        }
                    }
                )
            });
}

function statsLogisticRegression() {
    new Chart (
        document.getElementById("regression"),
        {
            type: 'scatter',
            data: {
                labels: "Hours",
                datasets: [
                    {
                        type: 'scatter',
                        label: 'Hours',
                        data: [
                            {x: 0.5, y:0 }, {x: 0.75, y:0 }, {x: 1.0, y:0 }, {x: 1.25, y:0 },
                            {x: 1.5, y:0 }, {x: 1.75, y:0 }, {x: 1.75, y:1 }, {x: 2.0, y:0 },
                            {x: 2.25, y:1 }, {x: 2.5, y:0 }, {x: 2.75, y:1 }, {x: 3.0, y:0 },
                            {x: 3.25, y:1 }, {x: 3.5, y:0 }, {x: 4.0, y:1 }, {x: 4.25, y:1 },
                            {x: 4.5, y:1 }, {x: 4.75, y:1 }, {x: 5.0, y:1 }, {x: 5.55, y:1 },
                         ],
                        backgroundColor: "rgba(33, 33, 33, 1)"
                    },
                      {
                                            type: 'line',
                                            label: 'Curve',
                                            data: [{x: 0.5, y:0 }, {x: 0.75, y:0.5 }, {x: 1.0, y:0.75 }, {x: 1.25, y:1 }],
                                            fill: false,
                                            backgroundColor: "rgba(33, 33, 33, 1)"
                        },
                ]
            }
        }
    )
}