const express = require("express");
const cors = require("cors");

const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const matchRoutes = require("./routes/matchRoutes");
const financeRoutes = require("./routes/financeRoutes"); 

const app = express();
app.use(cors({ origin: true }));


app.use("/auth", authRoutes);
app.use("/user", userRoutes);
app.use("/matches", matchRoutes);
app.use("/finance", financeRoutes);

module.exports = app;
