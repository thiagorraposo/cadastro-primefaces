// Requer playwright instalado fora do projeto; usa o Chrome do sistema.
const { chromium } = require(process.env.PLAYWRIGHT_MODULE || 'playwright');
const assert = require('node:assert/strict');
(async () => {
  const browser = await chromium.launch({executablePath: process.env.CHROME_BIN || '/usr/bin/google-chrome', headless: true, args:['--no-sandbox']});
  const page = await browser.newPage();
  const erros = [];
  page.on('pageerror', e => erros.push(e.message));
  const url = 'http://localhost:8082/cadastro/';
  const nome = 'Teste AJAX ' + Date.now();
  const senha = 'Senha de teste 123!';
  async function ajax(acao) {
    const resposta = page.waitForResponse(r => r.request().method() === 'POST' && r.url().includes('/cadastro/'));
    await acao();
    const r = await resposta;
    assert.equal(r.status(), 200);
    const texto = await r.text();
    assert.ok(texto.includes('<partial-response'));
    assert.ok(!texto.includes('<error>'), texto.slice(0, 500));
    await page.waitForFunction(() => PrimeFaces.ajax.Queue.isEmpty());
    return texto;
  }
  try {
    await page.goto(url);
    await page.waitForSelector('[id="cadastro:salvar"]');
    await ajax(() => page.locator('[id="cadastro:salvar"]').click());
    assert.match(await page.locator('#mensagens').innerText(), /Informe o nome/);
    await page.locator('[id="cadastro:nome"]').fill(nome);
    await page.locator('[id="cadastro:senha"]').fill(senha);
    await page.locator('[id="cadastro:descricao"]').fill('Registro automatizado <script> sem execução');
    await ajax(() => page.locator('[id="cadastro:salvar"]').click());
    assert.match(await page.locator('#mensagens').innerText(), /Selecione pelo menos um interesse/);
    assert.equal(await page.locator('[id="cadastro:senha"]').inputValue(), '');
    await page.locator('[id="cadastro:senha"]').fill(senha);
    await page.locator('.ui-picklist-source li').filter({hasText:/^Java$/}).dblclick();
    await page.waitForFunction(() => document.querySelector('.ui-picklist-target').textContent.includes('Java'));
    const resposta = await ajax(() => page.locator('[id="cadastro:salvar"]').click());
    assert.match(await page.locator('#mensagens').innerText(), /cadastrado com sucesso/);
    assert.ok(!resposta.includes(senha));
    assert.ok(!resposta.includes('pbkdf2-sha256'));
    assert.ok((await page.locator('[id="consulta:usuarios"]').innerText()).includes(nome));
    assert.equal(page.url(), url);
    assert.equal(await page.locator('[id="cadastro:senha"]').inputValue(), '');
    await page.reload();
    assert.ok((await page.locator('[id="consulta:usuarios"]').innerText()).includes(nome));
    await page.locator('[id="consulta:busca_input"]').fill(nome);
    await page.waitForSelector('.ui-autocomplete-item');
    await ajax(() => page.locator('.ui-autocomplete-item').filter({hasText:nome}).first().click());
    assert.ok((await page.locator('[id="consulta:usuarios"]').innerText()).includes(nome));
    const grid = page.locator('.ui-dataview-layout-options .ui-button').last();
    await ajax(() => grid.click());
    assert.ok(await page.locator('.ui-dataview-grid').count());
    await page.locator('[id="consulta:busca_input"]').fill('inexistente-' + Date.now());
    await ajax(() => page.locator('[id="consulta:buscar"]').click());
    assert.match(await page.locator('[id="consulta:usuarios"]').innerText(), /Nenhum usuário/);
    assert.deepEqual(erros, []);
    console.log(JSON.stringify({resultado:'OK',nome, testes:['validação obrigatórios','validação interesses no servidor','senha limpa após erro','cadastro AJAX','persistência após reload','autocomplete','dataView cartões','busca vazia','sem senha/hash na resposta','sem erros JavaScript']}));
  } finally { await browser.close(); }
})().catch(e => { console.error(e); process.exitCode = 1; });
