const express = require("express");
const cors = require("cors");

const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const matchRoutes = require("./routes/matchRoutes");
const financeRoutes = require("./routes/financeRoutes"); 
const paymentRoutes = require("./routes/paymentRoutes");

const app = express();
app.use(cors());


app.use("/auth", authRoutes);
app.use("/user", userRoutes);
app.use("/matches", matchRoutes);
app.use("/finance", financeRoutes);
app.use("/payment", paymentRoutes);

app.get('/test-cors', (req, res) => {
  res.json({ message: 'CORS test' });
});

module.exports = app;
