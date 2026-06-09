(() => {
  const report = window.__PUREBI_REPORT__ || {};
  const sidebar = document.getElementById('sidebar');

  document.querySelectorAll('[data-sidebar-toggle]').forEach((button) => {
    button.addEventListener('click', () => sidebar.classList.toggle('collapsed'));
  });

  document.querySelectorAll('[data-target]').forEach((item) => {
    item.addEventListener('click', () => {
      const target = document.getElementById(item.dataset.target);
      if (target) {
        target.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  });

  animateCounters();
  renderDonut('tests-donut', ['Passed', 'Failed', 'Skipped', 'Warnings'], [report.tests?.passed || 0, report.tests?.failed || 0, report.tests?.skipped || 0, report.tests?.warnings || 0]);
  renderDonut('steps-donut', ['Passed', 'Failed', 'Warnings', 'Info'], [report.steps?.passed || 0, report.steps?.failed || 0, report.steps?.warnings || 0, report.steps?.info || 0]);
  renderDonut('modules-donut', ['Passed', 'Failed', 'Warnings'], [report.modules?.passed || 0, report.modules?.failed || 0, report.modules?.warnings || 0]);
  renderExecutionChart('execution-donut');

  function animateCounters() {
    document.querySelectorAll('[data-counter]').forEach((node) => {
      const raw = node.textContent.trim();
      const numeric = Number.parseInt(raw.replace(/[^0-9]/g, ''), 10);
      if (!Number.isFinite(numeric)) {
        return;
      }
      let current = 0;
      const step = Math.max(1, Math.ceil(numeric / 40));
      const tick = () => {
        current += step;
        node.textContent = String(Math.min(current, numeric));
        if (current < numeric) {
          requestAnimationFrame(tick);
        }
      };
      tick();
    });
  }

  function renderDonut(canvasId, labels, values) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || typeof Chart === 'undefined') {
      return;
    }

    const colors = ['#22c55e', '#ef4444', '#f59e0b', '#38bdf8'];
    new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data: values,
          backgroundColor: colors.slice(0, values.length),
          borderWidth: 0,
          hoverOffset: 8,
        }],
      },
      options: {
        plugins: {
          legend: {
            display: true,
            labels: { color: '#f8fafc', usePointStyle: true, padding: 14 },
          },
        },
        cutout: '68%',
      },
    });
  }

  function renderExecutionChart(canvasId) {
    const canvas = document.getElementById(canvasId);
    if (!canvas || typeof Chart === 'undefined') {
      return;
    }

    const total = Math.max(1, report.execution?.totalMs || 1);
    const saved = Math.max(0, report.execution?.savedMs || 0);
    const elapsed = Math.max(1, total - saved);

    new Chart(canvas, {
      type: 'doughnut',
      data: {
        labels: ['Actual runtime', 'Parallel gain'],
        datasets: [{
          data: [elapsed, saved],
          backgroundColor: ['#38bdf8', '#22c55e'],
          borderWidth: 0,
        }],
      },
      options: {
        plugins: {
          legend: {
            display: true,
            labels: { color: '#f8fafc', usePointStyle: true, padding: 14 },
          },
        },
        cutout: '68%',
      },
    });
  }
})();