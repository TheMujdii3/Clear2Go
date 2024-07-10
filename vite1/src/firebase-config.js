// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
import {getDatabase} from "firebase/database";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyA51DFl-w_2WgBqe1KeginlkTFxlLVxKSY",
    authDomain: "clear2go1.firebaseapp.com",
    databaseURL: "https://clear2go1-default-rtdb.europe-west1.firebasedatabase.app",
    projectId: "clear2go1",
    storageBucket: "clear2go1.appspot.com",
    messagingSenderId: "341512314786",
    appId: "1:341512314786:web:75b39fbb8298ac6a301cfb",
    measurementId: "G-KRX68G6XWN"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
const db = getDatabase(firebaseConfig)
export {firebaseConfig,db,app}