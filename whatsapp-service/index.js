const { Client } = require('whatsapp-web.js');
const express = require('express');
const qrcode = require('qrcode-terminal');
const puppeteer = require('puppeteer');
const app = express();

app.use(express.json());

const client = new Client({
    puppeteer: {
        executablePath: puppeteer.executablePath(), // <- forzamos uso del Chromium de puppeteer
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    }
});

client.on('qr', (qr) => {
    qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
    console.log('✅ WhatsApp client is ready!');
});

client.initialize();

app.post('/send', async (req, res) => {
    const { number, message } = req.body;
    const chatId = `${number}@c.us`;

    try {
        await client.sendMessage(chatId, message);
        res.status(200).send({ status: 'Mensaje enviado' });
    } catch (error) {
        res.status(500).send({ error: error.message });
    }
});

const PORT = 3001;
app.listen(PORT, () => {
    console.log(`📡 Microservicio WhatsApp escuchando en http://localhost:${PORT}`);
});
