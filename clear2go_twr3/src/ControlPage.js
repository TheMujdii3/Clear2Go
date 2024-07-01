// src/components/ControlPage.js
import React, { useEffect, useState } from 'react';
import { ref, onValue } from 'firebase/database';
import { db } from './firebase-config';
import MapComponent from './MapComponent';
import './ControlPage.css';

const ControlPage = () => {
  const [planes, setPlanes] = useState([]);
  const [isMapExpanded, setIsMapExpanded] = useState(false);

  useEffect(() => {
    const planeRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');
    onValue(planeRef, (snapshot) => {
      const data = snapshot.val();
      if (data) {
        const planeArray = Object.keys(data).map(key => ({
          name: key,
          lat: data[key].lat,
          lng: data[key].lng,
          heading: data[key].heading || 0
        }));
        setPlanes(planeArray);
      }
    });
  }, []);

  const toggleMapSize = () => {
    setIsMapExpanded(!isMapExpanded);
  };

  return (
    <div className="control-page">

      <div className={`map-container ${isMapExpanded ? 'expanded' : ''}`}>
              <MapComponent planes={planes} />
              
        <button className="map-toggle-button" onClick={toggleMapSize}>
          {isMapExpanded ? 'Shrink Map' : 'Expand Map'}
        </button>
      </div>
      <div className="requests-section">
        <h2>Requests</h2>
        {/* Add requests handling code here */}
      </div>
    </div>
  );
};

export default ControlPage;
