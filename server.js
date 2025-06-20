const express = require("express");
const cors = require("cors");
const path = require("path");
require("dotenv").config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Routes
const authRoutes = require("./routes/auth");
const projectsRoutes = require("./routes/projects");

app.use("/api/auth", authRoutes);
app.use("/api/projects", projectsRoutes);

// Health check endpoint
app.get("/api/health", (req, res) => {
  res.json({ message: "Backend server is running!" });
});

// Serve static files from frontend build
app.use(express.static(path.join(__dirname, "../frontend/build")));

// Catch all handler for SPA routing
app.get("*", (req, res) => {
  res.sendFile(path.join(__dirname, "../frontend/build", "index.html"));
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ message: "Something went wrong!" });
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

module.exports = app;
