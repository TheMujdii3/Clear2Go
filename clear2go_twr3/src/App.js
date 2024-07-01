// src/App.js
import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import ControlPage from './ControlPage';
import ProfilePage from './ProfilePage'; // Create this component

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<ControlPage />} />
        <Route path="/profile" element={<ProfilePage />} />
      </Routes>
    </Router>
  );
}

export default App;
