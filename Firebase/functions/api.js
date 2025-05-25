const express = require("express");
const cors = require("cors");

const userRoutes = require("./routes/userRoutes");

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

app.use("/user", userRoutes);

module.exports = app;
