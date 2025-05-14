document.addEventListener('DOMContentLoaded', function () {
    const checkboxes = document.querySelectorAll('.form-check-input[type="checkbox"]');
    const selectAllBtn = document.getElementById('selectAllBtn');
    const deselectAllBtn = document.getElementById('deselectAllBtn');
    const exportBtn = document.getElementById('exportBtn');
    const exportPdfBtn = document.getElementById('exportPdfBtn');
    const generateChartBtn = document.getElementById('generateChartBtn');
    const chartContainer = document.getElementById('chartContainer');

    window.currentChart = null;

    // ==================== Колонки ====================
    function updateColumnVisibility(columnName, isVisible) {
        const displayValue = isVisible ? '' : 'none';
        document.querySelectorAll(`[data-column="${columnName}"]`).forEach(el => {
            el.style.display = displayValue;
        });
    }

    checkboxes.forEach(checkbox => {
        updateColumnVisibility(checkbox.dataset.column, checkbox.checked);
        checkbox.addEventListener('change', function () {
            updateColumnVisibility(this.dataset.column, this.checked);
        });
    });

    selectAllBtn.addEventListener('click', function (e) {
        e.preventDefault();
        checkboxes.forEach(checkbox => {
            checkbox.checked = true;
            updateColumnVisibility(checkbox.dataset.column, true);
        });
    });

    deselectAllBtn.addEventListener('click', function (e) {
        e.preventDefault();
        checkboxes.forEach(checkbox => {
            checkbox.checked = false;
            updateColumnVisibility(checkbox.dataset.column, false);
        });
    });

    // ==================== Экспорт ====================
    function getSelectedColumns() {
        return Array.from(document.querySelectorAll('.form-check-input[type="checkbox"]:checked'))
            .filter(cb => cb.dataset.column && cb.id !== 'includeChartInExport')
            .map(cb => cb.dataset.column);
    }

    function getSelectedAnalyticsFields() {
        return Array.from(document.querySelectorAll('.analytics-checkbox:checked'))
            .map(cb => cb.value);
    }

    exportBtn.addEventListener('click', function () {
        const selectedColumns = getSelectedColumns();
        const analyticsFields = getSelectedAnalyticsFields();

        if (analyticsFields.includes('warranty')) {
            if (!selectedColumns.includes('remainingWarrantyYears')) {
                selectedColumns.push('remainingWarrantyYears');
            }
            if (!selectedColumns.includes('remainingWarrantyComment')) {
                selectedColumns.push('remainingWarrantyComment');
            }
        }
        if (analyticsFields.includes('maintenance')) {
            if (!selectedColumns.includes('remainingMaintenanceYears')) {
                selectedColumns.push('remainingMaintenanceYears');
            }
            if (!selectedColumns.includes('remainingMaintenanceComment')) {
                selectedColumns.push('remainingMaintenanceComment');
            }
        }

        const params = new URLSearchParams();
        selectedColumns.forEach(col => params.append('columns', col));
        analyticsFields.forEach(field => params.append('analyticsFields', field));

        const includeChart = document.getElementById('includeChartInExport')?.checked;
        if (includeChart) {
            const chartType = document.getElementById('chartType').value;
            const groupByField = document.getElementById('groupByField').value;
            const subGroupByField = document.getElementById('subGroupByField').value;
            const valueField = document.getElementById('valueField').value;

            if (chartType && groupByField && valueField) {
                params.append('chartType', chartType);
                params.append('groupByField', groupByField);
                if (subGroupByField) {
                    params.append('subGroupByField', subGroupByField);
                }
                params.append('valueField', valueField);
            }
        }

        const filterForm = document.getElementById('filtersForm');
        if (filterForm) {
            new FormData(filterForm).forEach((value, key) => {
                if (value) params.append(key, value);
            });
        }

        window.location.href = '/resources/equipment/export-excel?' + params.toString();
    });

    exportPdfBtn?.addEventListener('click', function () {
        const params = new URLSearchParams();
        getSelectedColumns().forEach(col => params.append('columns', col));
        window.location.href = '/resources/equipment/export-pdf?' + params.toString();
    });

    // ==================== Графики ====================
    const groupByField = document.getElementById('groupByField');
    const subGroupByField = document.getElementById('subGroupByField');
    const chartTypeSelect = document.getElementById('chartType');

    groupByField.addEventListener('change', updateChartTypeOptions);
    subGroupByField.addEventListener('change', updateChartTypeOptions);
    updateChartTypeOptions();

    function updateChartTypeOptions() {
        const groupBy = groupByField.value;
        const subGroupBy = subGroupByField.value;

        const optionsMap = {
            single: ['bar', 'line', 'pie'],
            grouped: ['bar', 'line'],
            none: []
        };

        const availableTypes = !groupBy ? optionsMap.none :
            (groupBy && subGroupBy) ? optionsMap.grouped :
                optionsMap.single;

        Array.from(chartTypeSelect.options).forEach(option => {
            option.disabled = !availableTypes.includes(option.value);
            option.hidden = !availableTypes.includes(option.value);
        });

        if (!availableTypes.includes(chartTypeSelect.value)) {
            chartTypeSelect.value = availableTypes[0] || '';
        }
    }

    generateChartBtn.addEventListener('click', generateChart);

    async function generateChart() {
        const params = new URLSearchParams();
        const groupBy = groupByField.value;
        const subGroupBy = subGroupByField.value;
        const valueField = document.getElementById('valueField').value;

        if (!groupBy) {
            alert('Выберите поле группировки');
            return;
        }

        params.append('groupByField', groupBy);
        if (subGroupBy) params.append('subGroupByField', subGroupBy);
        params.append('valueField', valueField);

        const filterForm = document.getElementById('filtersForm');
        if (filterForm) {
            new FormData(filterForm).forEach((value, key) => {
                if (value) params.append(key, value);
            });
        }

        const response = await fetch(`/resources/chart-data?${params.toString()}`);
        if (!response.ok) {
            alert('Ошибка загрузки данных');
            return;
        }

        const data = await response.json();
        renderChart(data, chartTypeSelect.value);
    }

    function sortLabelsAndValues(labels, values) {
        const paired = labels.map((label, idx) => ({ label, value: values[idx] }));
        paired.sort((a, b) => {
            const aNum = Number(a.label);
            const bNum = Number(b.label);
            return !isNaN(aNum) && !isNaN(bNum)
                ? aNum - bNum
                : a.label.localeCompare(b.label);
        });

        return {
            labels: paired.map(p => p.label),
            values: paired.map(p => p.value)
        };
    }


function renderChart(data, chartType) {
    chartContainer.innerHTML = '<canvas id="analyticsChart"></canvas>';
    const ctx = document.getElementById('analyticsChart').getContext('2d');

    if (window.currentChart) {
        window.currentChart.destroy();
    }

    ctx.canvas.style.width = '100%';
    ctx.canvas.style.height = '400px';
    ctx.canvas.width = ctx.canvas.offsetWidth;
    ctx.canvas.height = ctx.canvas.offsetHeight;

    const mainColor = '#F3CAE2';
    const chartColors = chartType === 'pie'
        ? generatePieColors((data.values || []).length)
        : [mainColor];

    if (data.datasets) {
        // ← Стековый график (уже собранный)
        window.currentChart = new Chart(ctx, {
            type: chartType,
            data: {
                labels: data.labels,
                datasets: data.datasets
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
                    legend: { position: 'top' },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const label = context.dataset.label || '';
                                const value = context.parsed.y;
                                return `${label}: ${Number(value).toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: {
                    x: { stacked: true },
                    y: {
                        beginAtZero: true,
                        stacked: true,
                        ticks: {
                            callback: function (value) {
                                return Number(value).toLocaleString();
                            }
                        }
                    }
                }
            }
        });
    } else {
        // ← Обычный график (без подгрупп)
        const sorted = sortLabelsAndValues(data.labels || [], data.values || []);

        window.currentChart = new Chart(ctx, {
            type: chartType,
            data: {
                labels: sorted.labels,
                datasets: [{
                    label: data.datasetLabel || 'Данные',
                    data: sorted.values,
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
                        position: chartType === 'pie' ? 'right' : 'top'
                    },
                    tooltip: {
                        callbacks: {
                            label: function (context) {
                                const label = context.label || '';
                                const value = context.parsed;
                                return `${label}: ${Number(value).toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: chartType !== 'pie' ? {
                    x: { stacked: true },
                    y: {
                        beginAtZero: true,
                        stacked: true,
                        ticks: {
                            callback: function (value) {
                                return Number(value).toLocaleString();
                            }
                        }
                    }
                } : undefined
            }
        });
    }
}

    function generatePieColors(num) {
        const colors = [];
        for (let i = 0; i < num; i++) {
            colors.push(`hsl(${(i * 360) / num}, 70%, 60%)`);
        }
        return colors;
    }
});
