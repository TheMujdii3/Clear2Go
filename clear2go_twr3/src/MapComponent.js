import React, { useEffect, useRef } from 'react';
import { Loader } from '@googlemaps/js-api-loader';
import { getDatabase, ref, onValue } from 'firebase/database';

const GOOGLE_MAPS_API_KEY = 'AIzaSyApneNViwqMo-I_W5I_7hryJQcuwK_y8Uo';

const MapContainer = () => {
    const mapRef = useRef(null); // Reference for the map container

    useEffect(() => {
        const loader = new Loader({
            apiKey: GOOGLE_MAPS_API_KEY,
            version: 'weekly',
            id: '__googleMapsScriptId', // Unique identifier for the script
        });

        loader.load().then((google) => {
            const map = new google.maps.Map(mapRef.current, {
                center: { lat: 44.360977, lng: 25.934749 },
                zoom: 10,
            });

            const db = getDatabase();
            const positionsRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

            onValue(positionsRef, (snapshot) => {
                if (snapshot.exists()) {
                    snapshot.forEach((planeSnapshot) => {
                        const lat = planeSnapshot.child('lat').val();
                        const lng = planeSnapshot.child('lng').val();

                        // Create a marker for each plane
                        const marker = new google.maps.Marker({
                            position: { lat, lng },
                            map,
                            icon: {
                                url: './plane2.png', // Replace with your plane image path
                                scaledSize: new google.maps.Size(32, 32), // Adjust the size as needed
                            },
                        });
                    });
                }
            });

            return () => {
                // Clean up any resources if necessary
            };
        });
    }, []);

    return (
        <div style={{ width: '100%', height: '100vh', position: 'relative' }}>
            <div ref={mapRef} style={{ width: '100%', height: '100%' }} />
        </div>
    );
};

export default MapContainer;
