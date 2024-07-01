import { useState, useEffect } from 'react';
//import firebase from 'firebase/app';
import { ref, onValue } from 'firebase/database';
import {db} from './firebase-config'


function usePlaneData() {
  const [planes, setPlanes] = useState([]);
  
  useEffect(() => {
    //const database = firebase.database();
    const positionsRef = ref(db,'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

    onValue(positionsRef, (snapshot) => {
      if (snapshot.exists()) {
        const updatedPlanes = [];
        snapshot.forEach((planeSnapshot) => {
          const planeKey = planeSnapshot.key;
          const lat = planeSnapshot.child('lat').val();
          const lng = planeSnapshot.child('lng').val();
          const heading = planeSnapshot.child('heading').val();
          const newPosition = new window.google.maps.LatLng(lat, lng);

          updatedPlanes.push({
            id: planeKey, // Assuming 'planeKey' uniquely identifies the plane
            lat,
            lng,
            heading,
            position: newPosition, // Pre-construct the LatLng object
          });
        });
        setPlanes(updatedPlanes);
      }
    });

    // Cleanup function to detach listener when the component unmounts
    //return () => positionsRef.off();
  }, []);

  return planes;
}

export default usePlaneData;
