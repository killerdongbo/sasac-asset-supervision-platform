const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const diagrams = [
  { file: '01-three-domains.html', name: '三域架构全景图' },
  { file: '02-er-diagram.html', name: '核心实体关系图' },
  { file: '03-delivery-timeline.html', name: '分阶段交付路线图' },
  { file: '04-process-flows.html', name: '核心业务流程图' },
  { file: '05-code-deploy.html', name: '代码组织与部署架构' },
];

(async () => {
  const browser = await chromium.launch();

  for (const diagram of diagrams) {
    const page = await browser.newPage();
    const filePath = `file:///${__dirname}/${diagram.file}`.replace(/\\/g, '/');
    await page.goto(filePath, { waitUntil: 'networkidle' });

    // Get full page height
    const bodyHandle = await page.$('body');
    const boundingBox = await bodyHandle.boundingBox();
    const height = Math.ceil(boundingBox.height) + 40;

    await page.setViewportSize({ width: 1200, height });
    await page.screenshot({
      path: path.join(__dirname, `${diagram.file.replace('.html', '.png')}`),
      fullPage: true,
    });

    console.log(`✅ ${diagram.name} → ${diagram.file.replace('.html', '.png')}`);
    await page.close();
  }

  await browser.close();
  console.log('Done! All 5 diagrams saved as PNG.');
})();
