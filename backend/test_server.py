from fastapi.testclient import TestClient
from .server import app
import pytest

client = TestClient(app)

def test_read_root():
    response = client.get("/api/")
    assert response.status_code == 200
    assert response.json() == {"message": "Hello World"}

def test_create_status_check():
    response = client.post("/api/status", json={"client_name": "test_client"})
    assert response.status_code == 200
    json_response = response.json()
    assert json_response["client_name"] == "test_client"
    assert "id" in json_response
    assert "timestamp" in json_response

def test_get_status_checks():
    # First, create a status check to ensure there is data
    client.post("/api/status", json={"client_name": "test_client_2"})

    response = client.get("/api/status")
    assert response.status_code == 200
    json_response = response.json()
    assert isinstance(json_response, list)
    assert len(json_response) > 0
    assert "id" in json_response[0]
    assert "client_name" in json_response[0]
    assert "timestamp" in json_response[0]

@pytest.mark.parametrize("urls, expected_statuses", [
    (
        ["https://www.google.com", "https://www.github.com"],
        [
            {"url": "https://www.google.com", "status_code": 200, "status": "OK"},
            {"url": "https://www.github.com", "status_code": 200, "status": "OK"},
        ]
    ),
    (
        ["https://invalid-url-that-does-not-exist.com"],
        [
            {"url": "https://invalid-url-that-does-not-exist.com", "status_code": 0, "status": "Error"},
        ]
    ),
])
def test_check_url_status(urls, expected_statuses, monkeypatch):

    class MockResponse:
        def __init__(self, status_code):
            self.status_code = status_code

        def raise_for_status(self):
            if self.status_code != 200:
                raise Exception("HTTP Error")

    def mock_get(url, timeout):
        if "invalid" in url:
            raise Exception("RequestException")
        return MockResponse(200)

    monkeypatch.setattr("requests.get", mock_get)

    response = client.post("/api/status/check", json={"urls": urls})
    assert response.status_code == 200
    json_response = response.json()

    for i, result in enumerate(json_response):
        assert result["url"] == expected_statuses[i]["url"]
        assert result["status_code"] == expected_statuses[i]["status_code"]
        assert expected_statuses[i]["status"] in result["status"]
