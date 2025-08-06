import "./App.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import UrlChecker from "./UrlChecker";

function App() {
  return (
    <div className="App">
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<UrlChecker />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
