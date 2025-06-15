import requests

url = "http://localhost:8080/api/test-cases/upload-excel"
file_path = "test-cases.xlsx"
auth = ("admin", "admin")

with open(file_path, "rb") as f:
    files = {
        "file": (
            "test-cases.xlsx",
            f,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        )
    }
    response = requests.post(url, files=files, auth=auth)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {response.text}")
