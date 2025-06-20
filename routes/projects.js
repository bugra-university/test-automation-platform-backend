const express = require("express");
const router = express.Router();
const db = require("../config/database");

// GET /api/projects - Kullanıcının projelerini listele
router.get("/", async (req, res) => {
  try {
    // Burada auth middleware'den user bilgisi gelecek
    // Şimdilik hardcoded user_id = 1 kullanıyoruz
    const userId = req.user?.id || 1;

    const query = `
            SELECT p.*, u.username as owner_username
            FROM projects p
            JOIN users u ON p.owner_id = u.id
            WHERE p.owner_id = $1
            ORDER BY p.created_at DESC
        `;

    const result = await db.query(query, [userId]);

    res.json({
      success: true,
      projects: result.rows,
    });
  } catch (error) {
    console.error("Error fetching projects:", error);
    res.status(500).json({
      success: false,
      message: "Server error while fetching projects",
    });
  }
});

// POST /api/projects - Yeni proje oluştur
router.post("/", async (req, res) => {
  try {
    const { name, description } = req.body;

    // Validation
    if (!name || !description) {
      return res.status(400).json({
        success: false,
        message: "Project name and description are required",
      });
    }

    // Burada auth middleware'den user bilgisi gelecek
    // Şimdilik hardcoded user_id = 1 kullanıyoruz
    const userId = req.user?.id || 1;

    const query = `
            INSERT INTO projects (name, description, owner_id, created_at, updated_at)
            VALUES ($1, $2, $3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            RETURNING id, name, description, owner_id, created_at, updated_at
        `;

    const result = await db.query(query, [name, description, userId]);
    const newProject = result.rows[0];

    // Owner username'i de ekleyelim
    const userQuery = "SELECT username FROM users WHERE id = $1";
    const userResult = await db.query(userQuery, [userId]);
    newProject.owner_username = userResult.rows[0]?.username || "Unknown";

    res.status(201).json({
      success: true,
      project: newProject,
      message: "Project created successfully",
    });
  } catch (error) {
    console.error("Error creating project:", error);
    res.status(500).json({
      success: false,
      message: "Server error while creating project",
    });
  }
});

// GET /api/projects/:id - Belirli bir projeyi getir
router.get("/:id", async (req, res) => {
  try {
    const projectId = req.params.id;
    const userId = req.user?.id || 1;

    const query = `
            SELECT p.*, u.username as owner_username
            FROM projects p
            JOIN users u ON p.owner_id = u.id
            WHERE p.id = $1 AND p.owner_id = $2
        `;

    const result = await db.query(query, [projectId, userId]);

    if (result.rows.length === 0) {
      return res.status(404).json({
        success: false,
        message: "Project not found or access denied",
      });
    }

    res.json({
      success: true,
      project: result.rows[0],
    });
  } catch (error) {
    console.error("Error fetching project:", error);
    res.status(500).json({
      success: false,
      message: "Server error while fetching project",
    });
  }
});

module.exports = router;
