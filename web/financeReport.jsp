<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>财务报表</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
        }
        .chart-container {
            width: 80%;
            margin: 0 auto 40px;
        }
        h1 {
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<h1>财务报表</h1>

<div class="chart-container">
    <canvas id="salesByDate"></canvas>
</div>
<div class="chart-container">
    <canvas id="salesByType"></canvas>
</div>

<script>
    // 获取销售数据
    fetch('FinanceReportServlet')
        .then(response => response.json())
        .then(data => {
            // 按日期的销售数据
            const dateLabels = data.dateData.map(item => item.date);
            const dateSales = data.dateData.map(item => item.sales);

            const dateCtx = document.getElementById('salesByDate').getContext('2d');
            new Chart(dateCtx, {
                type: 'bar',
                data: {
                    labels: dateLabels,
                    datasets: [{
                        label: '按日期销售金额 (¥)',
                        data: dateSales,
                        backgroundColor: 'rgba(54, 162, 235, 0.5)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });

            // 按商品样式的销售数据
            const typeLabels = data.typeData.map(item => item.type);
            const typeSales = data.typeData.map(item => item.sales);

            const typeCtx = document.getElementById('salesByType').getContext('2d');
            new Chart(typeCtx, {
                type: 'bar',
                data: {
                    labels: typeLabels,
                    datasets: [{
                        label: '按商品样式销售金额 (¥)',
                        data: typeSales,
                        backgroundColor: 'rgba(255, 99, 132, 0.5)',
                        borderColor: 'rgba(255, 99, 132, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        })
        .catch(error => console.error('Error:', error));
</script>
</body>
</html>
