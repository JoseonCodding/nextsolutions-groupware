// src/App.js

import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, useNavigate } from "react-router-dom";
import About from "./About"; // 새 페이지 컴포넌트
import axios from "axios";

// Import Swiper React components
import { Swiper, SwiperSlide } from 'swiper/react';

// Import Swiper styles
import 'swiper/css';
import 'swiper/css/pagination';

import './styles.css';

// import required modules
import { Autoplay, Pagination } from 'swiper/modules';

function Home() {
  const [hello, setHello] = useState('');
  const [error, setError] = useState('');
  const [currentTime, setCurrentTime] = useState(''); // ✅ 이 줄 추가!
  const navigate = useNavigate(); // ⭐ 페이지 이동 훅

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [age, setAge] = useState('');

  const slides = [
    { id: 1, src: '/img/slide1.jpg', alt: 'Slide 1' },
    { id: 2, src: '/img/slide2.jpg', alt: 'Slide 2' },
    { id: 3, src: '/img/slide3.jpg', alt: 'Slide 3' },
    // ... 더 많은 이미지
  ];

  useEffect(() => {
    axios.get('http://localhost:8080/sample/api/test')
        .then((res) => {
          setHello(res.data);
        })
        .catch((err) => {
          setError(err.message);
        });


  const fetchTime = () => {
    axios.get('http://localhost:8080/sample/api/getTime')
      .then((response) => {
        setCurrentTime(response.data);
      })
      .catch((error) => {
        console.error('API 호출 중 오류 발생:', error);
      });
    };

    // 최초 1회 실행
    fetchTime();

    // 1초마다 시간 업데이트
    const intervalId = setInterval(fetchTime, 1000);

    // 컴포넌트 언마운트 시 인터벌 정리 -- 메모리 낭비 방지
    return () => clearInterval(intervalId);

  }, []);

  // ✅ 사용자 입력값 서버 전송 함수
  const handleRegister = async () => {
    const data = {
      name,
      email,
      age: parseInt(age)
    };

    try {
      const res = await axios.post("http://localhost:8080/sample/api/register", data);
      alert("등록 성공: " + res.data.message);
    } catch (err) {
      alert("등록 실패");
      console.error(err);
    }
  };

  return (
    <div className="App">
      <h2>GSITM 부트캠프 입소를 환영합니다. : {hello}</h2>

      {/* ✅ 사용자 정보 입력 영역 */}
      <h3>사용자 등록</h3>
      <input placeholder="이름" value={name} onChange={e => setName(e.target.value)} />
      <br />
      <input placeholder="이메일" value={email} onChange={e => setEmail(e.target.value)} />
      <br />
      <input placeholder="나이" type="number" value={age} onChange={e => setAge(e.target.value)} />
      <br />
      <button onClick={handleRegister}>서버에 등록하기</button>

      <br /><br />

      {error && <p>Error: {error}</p>}

      <img src="/img/lion.jpg" alt="라이언 이미지" style={{ width: '50px', height: 'auto' }}/>
      <h1>현재 시간</h1>
      <p>{currentTime}</p>
      <br /><br />
      <button onClick={() => navigate('/about')}>About 페이지로 이동</button>
    
      <>
      <Swiper
        pagination={true}
        modules={[Pagination, Autoplay]}
        className="mySwiper"
        autoplay={{
          delay: 2000,     // 2000ms = 2초
          disableOnInteraction: false, // 사용자가 슬라이드를 건드려도 자동재생 유지
        }}
        style={{ height: '300px', width: '100%' }}  // 명확히 높이 지정
      >
        <SwiperSlide>Slide 1</SwiperSlide>
        <SwiperSlide>Slide 2</SwiperSlide>
        <SwiperSlide>Slide 3</SwiperSlide>
        <SwiperSlide>Slide 4</SwiperSlide>
        <SwiperSlide>Slide 5</SwiperSlide>
        <SwiperSlide>Slide 6</SwiperSlide>
        <SwiperSlide>Slide 7</SwiperSlide>
        <SwiperSlide>Slide 8</SwiperSlide>
        <SwiperSlide>Slide 9</SwiperSlide>
      </Swiper>
    </>
    <br></br>
    <Swiper pagination={true} modules={[Pagination]} className="mySwiper" style={{ height: '300px' }}>
      {slides.map(({ id, src, alt }) => (
        <SwiperSlide key={id}>
          <img src={src} alt={alt} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
        </SwiperSlide>
      ))}
    </Swiper>
    
    
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
