import React, { useEffect, useRef, useState } from 'react';
import { GoogleMapsOverlay } from '@deck.gl/google-maps';
import { Loader } from '@googlemaps/js-api-loader';
import { BitmapLayer } from '@deck.gl/layers';
import { getDatabase, ref, onValue } from 'firebase/database';

const GOOGLE_MAPS_API_KEY = 'AIzaSyApneNViwqMo-I_W5I_7hryJQcuwK_y8Uo';  // Replace with your API key

const MapContainer = () => {
    const mapRef = useRef(null);  // Reference for the map container
    const overlayRef = useRef(null);  // Reference for Deck.gl overlay
    const [planes, setPlanes] = useState({});  // State for planes data

    // Initialize the Google Maps and Deck.gl overlay
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

            overlayRef.current = new GoogleMapsOverlay({
                layers: [],
            });

            overlayRef.current.setMap(map);

            return () => overlayRef.current.setMap(null);
        });
    }, []);

    // Fetch planes data from Firebase and update state
    useEffect(() => {
        const db = getDatabase();
        const positionsRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

        const unsubscribe = onValue(positionsRef, (snapshot) => {
            if (snapshot.exists()) {
                const updatedPlanes = {};
                snapshot.forEach((planeSnapshot) => {
                    const planeKey = planeSnapshot.key;
                    const lat = planeSnapshot.child('lat').val();
                    const lng = planeSnapshot.child('lng').val();
                    const heading = planeSnapshot.child('heading').val();

                    updatedPlanes[planeKey] = {
                        lat,
                        lng,
                        heading,
                    };
                });
                setPlanes(updatedPlanes);
            }
        });

        return () => unsubscribe();
    }, []);

    // Update Deck.gl overlay layers when planes data changes
    useEffect(() => {
        if (overlayRef.current) {
            const layers = Object.keys(planes).map((planeKey) => {
                const plane = planes[planeKey];
                return new BitmapLayer({
                    id: `plane-${planeKey}`,
                    image:'./plane2.png',  // Replace with the path to your plane image
                    bounds: [
                        [plane.lng - 0.01, plane.lat - 0.01],
                        [plane.lng + 0.01, plane.lat + 0.01],
                    ],  // Example bounds around the plane's location
                    opacity: 1,
                    getPixelOffset: () => [0, 0],
                });
            });

            overlayRef.current.setProps({ layers });
        }
    }, [planes]);

    return (
        <div style={{ width: '100%', height: '100vh' }}>
            <div ref={mapRef} style={{ width: '100%', height: '100%' }} />
        </div>
    );
};

export default MapContainer;
