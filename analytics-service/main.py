from fastapi import FastAPI, HTTPException
from sqlalchemy import create_engine, text
from dotenv import load_dotenv
import os

load_dotenv()

app = FastAPI(title="TaskFlow Analytics Service")

DATABASE_URL = "mysql+pymysql://root:root@localhost:3306/taskflow"
engine = create_engine(DATABASE_URL)


@app.get("/")
def root():
    return {"service": "analytics-service", "status": "running"}


@app.get("/analytics/project/{project_id}/task-summary")
def task_summary(project_id: int):
    with engine.connect() as conn:
        result = conn.execute(
            text("""
                SELECT status, COUNT(*) as count
                FROM tasks
                WHERE project_id = :project_id
                GROUP BY status
            """),
            {"project_id": project_id}
        )
        rows = result.fetchall()

    if not rows:
        raise HTTPException(status_code=404, detail="No tasks found for this project")

    summary = {row[0]: row[1] for row in rows}
    total = sum(summary.values())

    return {
        "project_id": project_id,
        "total_tasks": total,
        "by_status": summary
    }


@app.get("/analytics/user/{user_id}/workload")
def user_workload(user_id: int):
    with engine.connect() as conn:
        result = conn.execute(
            text("""
                SELECT status, COUNT(*) as count
                FROM tasks
                WHERE assignee_id = :user_id
                GROUP BY status
            """),
            {"user_id": user_id}
        )
        rows = result.fetchall()

    summary = {row[0]: row[1] for row in rows}
    total = sum(summary.values())

    return {
        "user_id": user_id,
        "total_assigned": total,
        "by_status": summary
    }
