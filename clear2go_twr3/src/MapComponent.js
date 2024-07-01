// src/components/MapComponent.js
import React, { useEffect, useRef, useState } from 'react';
import { GoogleMap, LoadScript, Circle } from '@react-google-maps/api';
import { ref, onValue } from 'firebase/database';
import { db } from './firebase-config'; // Ensure firebase-config.js exports `db`

const mapContainerStyle = {
    width: '100%',
    height: '100%'
};

const center = {
    lat: 44.360977,
    lng: 25.934749
};

const options = {
    disableDefaultUI: true,
    zoomControl: true,
};

const planeImage = `./plane2.png`; // Ensure this image exists

const MapComponent = () => {
    const [planes, setPlanes] = useState([]);
    const mapRef = useRef(null);
    const overlaysRef = useRef({});
    const previousPositionsRef = useRef({});


    // Fetch data from Firebase
    useEffect(() => {
        const positionsRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

        const unsubscribe = onValue(positionsRef, (snapshot) => {
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
        return () => unsubscribe();
    }, []); // Empty dependency array ensures this effect runs once

    // Create and update ground overlays
    useEffect(() => {
        if (mapRef.current && planes.length > 0) {
            planes.forEach(plane => {
                const position = new window.google.maps.LatLng(plane.lat, plane.lng);
                const rotation = plane.heading;

                if (overlaysRef.current[plane.id]) {
                    const overlay = overlaysRef.current[plane.id];
                    animateOverlay(overlay, position, rotation, plane.id);
                } else {
                    createOverlay(plane);
                }
            });
        }
    }, [planes]);

    const createOverlay = (plane) => {
        const imageBounds = {
            north: plane.lat + 0.005,
            south: plane.lat - 0.005,
            east: plane.lng + 0.005,
            west: plane.lng - 0.005,
        };

        const newOverlay = new window.google.maps.GroundOverlay(
            planeImage,
            imageBounds
        );

        newOverlay.setMap(mapRef.current);
        newOverlay.set('rotation', plane.heading);

        overlaysRef.current[plane.id] = newOverlay;
        previousPositionsRef.current[plane.id] = new window.google.maps.LatLng(plane.lat, plane.lng);
    };
    //bambambam
    const animateOverlay = (overlay, toPosition, toRotation, planeId) => {
        const fromPosition = previousPositionsRef.current[planeId];
        const fromRotation = overlay.get('rotation') || 0;

        if (fromPosition.equals(toPosition) && fromRotation === toRotation) {
            return;
        }

        const startTime = performance.now();
        const duration = 500; // Duration in milliseconds

        const animateStep = () => {
            const currentTime = performance.now();
            const progress = Math.min((currentTime - startTime) / duration, 1);
            const lat = fromPosition.lat() + (toPosition.lat() - fromPosition.lat()) * progress;
            const lng = fromPosition.lng() + (toPosition.lng() - fromPosition.lng()) * progress;
            const rotation = fromRotation + (toRotation - fromRotation) * progress;

            const newBounds = new window.google.maps.LatLngBounds(
                new window.google.maps.LatLng(lat - 0.005, lng - 0.005),
                new window.google.maps.LatLng(lat + 0.005, lng + 0.005)
            );

            overlay.setBounds(newBounds);
            overlay.set('rotation', rotation);

            if (progress < 1) {
                requestAnimationFrame(animateStep);
            } else {
                previousPositionsRef.current[planeId] = toPosition;
            }
        };

        requestAnimationFrame(animateStep);
    };

    return (
        <LoadScript googleMapsApiKey={'AIzaSyApneNViwqMo-I_W5I_7hryJQcuwK_y8Uo'}>
            <GoogleMap
                mapContainerStyle={mapContainerStyle}
                zoom={10}
                center={center}
                options={options}
                onLoad={map => (mapRef.current = map)}
            >
                <Circle center={center} radius={6000} options={{ fillColor: "#f00", strokeColor: "#f00" }} />
                {/* Overlays will be added programmatically */}
            </GoogleMap>
        </LoadScript>
    );
};

export default MapComponent;
