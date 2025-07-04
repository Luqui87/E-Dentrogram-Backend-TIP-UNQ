const { Client,MessageMedia } = require('whatsapp-web.js');
const express = require('express');
const qrcode = require('qrcode-terminal');
const puppeteer = require('puppeteer');
const mime = require('mime-types');
const multer = require('multer');
const upload = multer();
const app = express();

app.use(express.json({ limit: '20mb' }));

let lastQR = null;
let isReady = false;

const client = new Client({
    puppeteer: {
        executablePath: puppeteer.executablePath(),
        args: ['--no-sandbox', '--disable-setuid-sandbox']
    }
});

client.on('qr', (qr) => {
    lastQR = qr;
    isReady = false;
    qrcode.generate(qr, { small: true });
});

client.on('ready', () => {
    console.log('✅ WhatsApp client is ready!');
    isReady = true;
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

app.get('/qr', (req, res) => {
    if (isReady) {
        res.status(404).send({ error: 'Cliente listo, QR no disponible' });
    } else if (lastQR) {
        res.status(200).send({ qr: lastQR });
    } else {
        res.status(404).send({ error: 'QR no disponible' });
    }
});


app.post('/send-multiple-files', upload.array('files'), async (req, res) => {
    const { number, message } = req.body;
    const chatId = `${number}@c.us`;

    try {

        if (req.files && req.files.length > 0) {
            for (const file of req.files) {
                const mimeType = file.mimetype;
                const base64 = file.buffer.toString('base64');
                const media = new MessageMedia(mimeType, base64, file.originalname);
                await client.sendMessage(chatId, media);
            }
        }

        if (message) {
            await client.sendMessage(chatId, message);
        }

        res.status(200).send({ status: 'Mensaje y archivos enviados' });

    } catch (err) {
        console.error('Error al enviar mensaje:', err);
        res.status(500).send({ error: err.message });
    }
});


const PORT = 3001;
app.listen(PORT, () => {
    console.log(`📡 Microservicio WhatsApp escuchando en http://localhost:${PORT}`);
});
