const express = require('express');
const { importFirestoreFinalData } = require('../import_data/importFirestore');
const router = express.Router();

router.post('/import-sample', async (req, res) => {
  try {
    const result = await importFirestoreFinalData();
    res.send({ message: result });
  } catch (err) {
    console.error(err);
    res.status(500).send({ error: 'Import failed' });
  }
});

module.exports = router;
