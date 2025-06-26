// src/About.jsx

import React from 'react';
import { useNavigate } from "react-router-dom";

function About() {
  const navigate = useNavigate();

  return (
    <div>
      <h2>About 페이지입니다</h2>
      <button onClick={() => navigate('/')}>홈으로 돌아가기</button>
    </div>
  );
}

export default About;
