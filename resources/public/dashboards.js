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
