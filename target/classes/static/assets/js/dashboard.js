/* globals Chart:false, feather:false */

(function () {
  'use strict';

  feather.replace();

  // Graphs
  var ctx = document.getElementById('myChart');
  // eslint-disable-next-line no-unused-vars
  var myChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: [
        'Sunday',
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday',
      ],
      datasets: [
        {
          data: [1, 6, 15, 20, 40, 60, 80],
          lineTension: 0,
          backgroundColor: 'transparent',
          borderColor: '#007bff',
          borderWidth: 4,
          pointBackgroundColor: '#007bff',
        },
        {
          data: [3, 6, 0, 3, 2, 5, 3],
          lineTension: 0,
          backgroundColor: 'transparent',
          borderColor: '#c50000',
          borderWidth: 4,
          pointBackgroundColor: '#c50000',
        },
      ],
    },
    options: {
      scales: {
        yAxes: [
          {
            ticks: {
              beginAtZero: false,
            },
          },
        ],
      },
      legend: {
        display: false,
      },
    },
  });
})();
