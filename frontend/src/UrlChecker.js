import { useState } from "react";
import axios from "axios";

const BACKEND_URL = process.env.REACT_APP_BACKEND_URL;
const API = `${BACKEND_URL}/api`;

const UrlChecker = () => {
  const [urls, setUrls] = useState("");
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleCheckUrls = async () => {
    setLoading(true);
    try {
      const urlList = urls.split("\n").filter((url) => url.trim() !== "");
      const response = await axios.post(`${API}/status/check`, { urls: urlList });
      setResults(response.data);
    } catch (e) {
      console.error(e, "errored out requesting /api/status/check");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">URL Status Checker</h1>
      <textarea
        className="w-full h-40 p-2 border rounded"
        placeholder="Enter one URL per line"
        value={urls}
        onChange={(e) => setUrls(e.target.value)}
      />
      <button
        className="mt-4 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 disabled:bg-gray-400"
        onClick={handleCheckUrls}
        disabled={loading}
      >
        {loading ? "Checking..." : "Check URLs"}
      </button>

      {results.length > 0 && (
        <table className="mt-4 w-full border-collapse">
          <thead>
            <tr>
              <th className="border p-2">URL</th>
              <th className="border p-2">Status Code</th>
              <th className="border p-2">Status</th>
            </tr>
          </thead>
          <tbody>
            {results.map((result, index) => (
              <tr key={index}>
                <td className="border p-2">{result.url}</td>
                <td className="border p-2">{result.status_code}</td>
                <td className="border p-2">{result.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default UrlChecker;
