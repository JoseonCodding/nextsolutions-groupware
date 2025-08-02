import axios from "axios";


/**
 * 서버에서 현재 시간을 가져오는 함수
 * @returns {Promise} 서버 응답(현재 시간 문자열)을 담은 Promise 객체
 */
export const fetchCurrentTime = () => axios.get("http://localhost:8080/sampleReactMvc/api/getTime");

// 선언 스타일
// export function fetchCurrentTime() {
//     return axios.get("http://localhost:8080/sampleReactMvc/api/getTime");
//   }
  

/**
 * 
 * @param {*} data 
 * @returns 
 */
export const registerUser = (data) => axios.post("http://localhost:8080/sampleReactMvc/api/save", data);