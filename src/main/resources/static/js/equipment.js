document.addEventListener('DOMContentLoaded', function() {
     // Ваш JavaScript код
     const checkboxes = document.querySelectorAll('.form-check-input[type="checkbox"]');
     const selectAllBtn = document.getElementById('selectAllBtn');
     const deselectAllBtn = document.getElementById('deselectAllBtn');
     const exportBtn = document.getElementById('exportBtn');
     const exportPdfBtn = document.getElementById('exportPdfBtn');
     const generateChartBtn = document.getElementById('generateChartBtn');
     const chartContainer = document.getElementById('chartContainer');

     let currentChart = null;

     // ==================== Управление колонками ====================
     function updateChartTypeOptions() {
         const groupBy = groupByField.value;
         const subGroupBy = subGroupByField.value;

         // Если выбраны обе группировки, исключаем круговую диаграмму
         if (groupBy && subGroupBy) {
             // Запрещаем выбор круговой диаграммы
             Array.from(chartTypeSelect.options).forEach(option => {
                 if (option.value === 'pie') {
                     option.disabled = true;
                 }
             });
         } else {
             // Разрешаем выбор круговой диаграммы
             Array.from(chartTypeSelect.options).forEach(option => {
                 if (option.value === 'pie') {
                     option.disabled = false;
                 }
             });
         }
     }

     // Слушаем изменения на полях группировки
     const groupByField = document.getElementById('groupByField');
     const subGroupByField = document.getElementById('subGroupByField');
     const chartTypeSelect = document.getElementById('chartType');

     groupByField.addEventListener('change', updateChartTypeOptions);
     subGroupByField.addEventListener('change', updateChartTypeOptions);

     // Инициализируем сразу
     updateChartTypeOptions();

     // ==================== Видимость колонок ====================
     function updateColumnVisibility(columnName, isVisible) {
         const displayValue = isVisible ? '' : 'none';
         document.querySelectorAll(`[data-column="${columnName}"]`).forEach(el => {
             el.style.display = displayValue;
         });
     }

     // Инициализация видимости колонок
     checkboxes.forEach(checkbox => {
         updateColumnVisibility(checkbox.dataset.column, checkbox.checked);

         checkbox.addEventListener('change', function() {
             updateColumnVisibility(this.dataset.column, this.checked);
         });
     });

     // Кнопки "Выбрать все"/"Сбросить"
     selectAllBtn.addEventListener('click', function(e) {
         e.preventDefault();
         checkboxes.forEach(checkbox => {
             checkbox.checked = true;
             updateColumnVisibility(checkbox.dataset.column, true);
         });
     });

     deselectAllBtn.addEventListener('click', function(e) {
         e.preventDefault();
         checkboxes.forEach(checkbox => {
             checkbox.checked = false;
             updateColumnVisibility(checkbox.dataset.column, false);
         });
     });

     // ==================== Экспорт данных ====================
     function getExportParams() {
         const selectedColumns = Array.from(document.querySelectorAll('.form-check-input[type="checkbox"]:checked'))
             .filter(checkbox => !checkbox.id || checkbox.id !== 'includeChartInExport')
             .map(checkbox => checkbox.dataset.column);

         if (selectedColumns.length === 0) {
             alert('Пожалуйста, выберите хотя бы одну колонку для экспорта');
             return null;
         }

         const params = new URLSearchParams();
         const filterForm = document.getElementById('filtersForm');

         // Добавляем параметры фильтрации
         if (filterForm) {
             new FormData(filterForm).forEach((value, key) => {
                 if (value) params.append(key, value);
             });
         }

         // Добавляем выбранные колонки
         selectedColumns.forEach(col => params.append('columns', col));

         // Добавляем параметры графика, если включена опция
         const includeChart = document.getElementById('includeChartInExport').checked;
         if (includeChart) {
             const chartType = document.getElementById('chartType').value;
             const groupByField = document.getElementById('groupByField').value;
             const subGroupByField = document.getElementById('subGroupByField').value;
             const valueField = document.getElementById('valueField').value;

             if (chartType && groupByField && valueField) {
                 params.append('chartType', chartType);
                 params.append('groupByField', groupByField);
                 if (subGroupByField) {
                     params.append('subGroupByField', subGroupByField); // добавляем второй параметр
                 }
                 params.append('valueField', valueField);
             }
         }

         return params;
     }

     // Обработчики кнопок экспорта
     exportBtn.addEventListener('click', function() {
         const params = getExportParams();
         if (params) {
             window.location.href = `/resources/equipment/export-excel?${params.toString()}`;
         }
     });

     exportPdfBtn.addEventListener('click', function() {
         const params = getExportParams();
         if (params) {
             window.location.href = `/resources/equipment/export-pdf?${params.toString()}`;
         }
     });

     // ==================== Работа с графиками ====================
     generateChartBtn.addEventListener('click', generateChart);

     async function generateChart() {
         const groupByField = document.getElementById('groupByField').value;
         const subGroupByField = document.getElementById('subGroupByField').value;
         const valueField = document.getElementById('valueField').value;
         const chartType = document.getElementById('chartType').value;

         // Валидация
         if (!groupByField) {
             showChartError('Пожалуйста, выберите поле для группировки');
             return;
         }

         try {
             showChartLoading();

             const params = new URLSearchParams();
             params.append('groupByField', groupByField);
             if (subGroupByField) {
                 params.append('subGroupByField', subGroupByField); // добавляем второй параметр
             }
             params.append('valueField', valueField);

             // Добавляем параметры фильтрации
             const filterForm = document.getElementById('filtersForm');
             if (filterForm) {
                 new FormData(filterForm).forEach((value, key) => {
                     if (value) params.append(key, value);
                 });
             }

             // Запрос данных
             const response = await fetch(`/resources/chart-data?${params.toString()}`);

             if (!response.ok) {
                 throw new Error(`Ошибка сервера: ${response.status}`);
             }

             const chartData = await response.json();
             renderChart(chartData, chartType);

         } catch (error) {
             console.error("Ошибка при создании графика:", error);
             showChartError(`Ошибка при загрузке данных: ${error.message}`);
         }
     }

     function showChartLoading() {
         chartContainer.innerHTML = `
             <div class="chart-loading">
                 <div class="spinner-border text-primary" role="status"></div>
                 <p class="mt-2">Загрузка данных...</p>
             </div>
             <canvas id="analyticsChart" style="display: none"></canvas>
         `;
     }

     function showChartError(message) {
         const canvas = chartContainer.querySelector('#analyticsChart');
         if (canvas) {
             canvas.style.display = 'none';
         }

         const errorDiv = document.createElement('div');
         errorDiv.className = 'alert alert-danger';
         errorDiv.textContent = message;

         chartContainer.insertBefore(errorDiv, chartContainer.firstChild);

         setTimeout(() => {
             errorDiv.remove();
         }, 5000);
     }

     function renderChart(data, chartType) {
         // Очищаем контейнер
         chartContainer.innerHTML = '<canvas id="analyticsChart"></canvas>';
         const ctx = document.getElementById('analyticsChart').getContext('2d');

         // Удаляем предыдущий график
         if (window.currentChart) {
             window.currentChart.destroy();
         }

         // Настройка адаптивности
         ctx.canvas.style.width = '100%';
         ctx.canvas.style.height = '400px';
         ctx.canvas.width = ctx.canvas.offsetWidth;
         ctx.canvas.height = ctx.canvas.offsetHeight;

         const mainColor = '#F3CAE2';

         // Выбор цветов для графика
         const chartColors = chartType === 'pie'
             ? generatePieColors(data.values.length) // Убедитесь, что передаете правильное количество
             : [mainColor];

         // Для стэк-бар графика
         if (chartType === 'bar' && data.subGroupByField) {
             // Группировка данных по подгруппам
             const groupedData = groupDataBySubGroup(data.labels, data.values, data.subGroupByField);

             // Подготовка нескольких наборов данных для стэк-бар
             const datasets = Object.keys(groupedData).map(subGroup => {
                 return {
                     label: subGroup,  // Название подгруппы
                     data: groupedData[subGroup],
                     backgroundColor: chartColors // Можете добавить разные цвета для разных подгрупп
                 };
             });

             window.currentChart = new Chart(ctx, {
                 type: 'bar',
                 data: {
                     labels: data.labels,  // метки для оси X (группировка)
                     datasets: datasets     // Данные для графика, включая подгруппы
                 },
                 options: {
                     responsive: true,
                     maintainAspectRatio: false,
                     plugins: {
                         title: {
                             display: true,
                             text: data.title || 'Анализ данных',
                             font: { size: 16 }
                         },
                         legend: {
                             position: 'top',
                         },
                         tooltip: {
                             callbacks: {
                                 label: function(context) {
                                     const label = context.label || '';
                                     const value = context.parsed.y;
                                     return `${label}: ${Number(value).toLocaleString()}`;
                                 }
                             }
                         }
                     },
                     scales: {
                         y: {
                             beginAtZero: true,
                             stacked: true, // Включаем stacked bar
                             ticks: {
                                 callback: function(value) {
                                     return Number(value).toLocaleString();
                                 }
                             }
                         }
                     },
                     animation: {
                         duration: data.labels.length > 20 ? 0 : 1000
                     }
                 }
             });
         } else {
             // Простое отображение для других типов графиков
             window.currentChart = new Chart(ctx, {
                 type: chartType,
                 data: {
                     labels: data.labels || [],  // метки для оси X (группировка)
                     datasets: data.datasets || [{
                         label: data.datasetLabel || 'Данные',
                         data: data.values || [],
                         backgroundColor: chartColors
                     }]
                 },
                 options: {
                     responsive: true,
                     maintainAspectRatio: false,
                     plugins: {
                         title: {
                             display: true,
                             text: data.title || 'Анализ данных',
                             font: { size: 16 }
                         },
                         legend: {
                             position: chartType === 'pie' ? 'right' : 'top',
                         },
                         tooltip: {
                             callbacks: {
                                 label: function(context) {
                                     const label = context.label || '';
                                     const value = context.parsed;
                                     return `${label}: ${Number(value).toLocaleString()}`;
                                 }
                             }
                         }
                     },
                     scales: chartType !== 'pie' ? {
                         x: {
                             stacked: true
                         },
                         y: {
                             beginAtZero: true,
                             stacked: true,
                             ticks: {
                                 callback: function(value) {
                                     return Number(value).toLocaleString();
                                 }
                             }
                         }
                     } : undefined,
                     animation: {
                         duration: data.labels.length > 20 ? 0 : 1000
                     }
                 }
             });
         }
     }

            // Функция для группировки данных по подгруппе
            function groupDataBySubGroup(labels, values, subGroupByField) {
                const grouped = {};
                labels.forEach((label, index) => {
                    const subGroup = values[index][subGroupByField];  // предполагаем, что в данных есть это поле
                    if (!grouped[subGroup]) {
                        grouped[subGroup] = [];
                    }
                    grouped[subGroup].push(values[index].value); // значение из нужного поля
                });
                return grouped;
            }

    function generatePieColors(num) {
        const colors = [];
        for (let i = 0; i < num; i++) {
            colors.push(`hsl(${(i * 360) / num}, 70%, 60%)`);
        }
        return colors;
    }
    });
