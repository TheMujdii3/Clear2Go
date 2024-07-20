import React, { Fragment, useEffect, useRef, useState } from "react";
import { useLoadScript } from "@react-google-maps/api";
import { initializeApp } from "firebase/app";
import { getDatabase, ref, onValue } from "firebase/database";
import planeImage from './plane2.png';

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

function App() {
    const { isLoaded } = useLoadScript({
        googleMapsApiKey: import.meta.env.VITE_MAP_API_KEY, // Replace with your API key
    });
    const mapRef = useRef(null);
    const [map, setMap] = useState(null);
    const [planeOverlays, setPlaneOverlays] = useState(new Map());
    const [planesMap, setPlanesMap] = useState(new Map());

    useEffect(() => {
        if (!isLoaded || !mapRef.current) return;

        const initMap = () => {
            const newMap = new window.google.maps.Map(mapRef.current, {
                center: { lat: 44.3947365, lng: 25.6898045 },
                zoom: 10,
            });
            setMap(newMap);
        };

        initMap();

        return () => {
            if (map) {
                mapRef.current = null;
            }
        };
    }, [isLoaded, map]);

    useEffect(() => {
        if (!map) return;

        // Define CustomOverlay class
        class CustomOverlay extends window.google.maps.OverlayView {
            constructor(bounds, image, heading) {
                super();
                this.bounds = bounds;
                this.image = image;
                this.heading = heading;
                this.div = null;
            }

            onAdd() {
                const div = document.createElement('div');
                div.style.borderStyle = 'none';
                div.style.borderWidth = '0px';
                div.style.position = 'absolute';

                const img = document.createElement('img');
                img.src = this.image;
                img.style.width = '100%';
                img.style.height = '100%';
                img.style.position = 'absolute';
                img.style.visibility = 'visible';
                img.style.transform = `rotate(${this.heading}deg)`;

                div.appendChild(img);
                this.div = div;

                const panes = this.getPanes();
                panes.overlayLayer.appendChild(div);
            }

            draw() {
                if (!this.div) return;

                const overlayProjection = this.getProjection();
                const sw = overlayProjection.fromLatLngToDivPixel(
                    new window.google.maps.LatLng(this.bounds.south, this.bounds.west)
                );
                const ne = overlayProjection.fromLatLngToDivPixel(
                    new window.google.maps.LatLng(this.bounds.north, this.bounds.east)
                );

                const div = this.div;
                div.style.left = sw.x + 'px';
                div.style.top = ne.y + 'px';
                div.style.width = (ne.x - sw.x) + 'px';
                div.style.height = (sw.y - ne.y) + 'px';
            }

            onRemove() {
                if (this.div && this.div.parentNode) {
                    this.div.parentNode.removeChild(this.div);
                    this.div = null;
                }
            }

            updateBounds(newBounds) {
                this.bounds = newBounds;
                this.draw();
            }

            updateHeading(newHeading) {
                this.heading = newHeading;
                if (this.div) {
                    const img = this.div.querySelector('img');
                    if (img) {
                        img.style.transform = `rotate(${this.heading}deg)`;
                    }
                }
            }

            animateToPosition(newBounds, newHeading) {
                const startBounds = this.bounds;
                const startHeading = this.heading;
                const startTime = performance.now();
                const duration = 500; // Animation duration in ms

                const animate = (time) => {
                    const t = Math.min((time - startTime) / duration, 1); // Linear interpolation factor (0 to 1)

                    const interpolate = (start, end) => start + t * (end - start);

                    const currentBounds = {
                        north: interpolate(startBounds.north, newBounds.north),
                        south: interpolate(startBounds.south, newBounds.south),
                        east: interpolate(startBounds.east, newBounds.east),
                        west: interpolate(startBounds.west, newBounds.west),
                    };

                    const currentHeading = interpolate(startHeading, newHeading);

                    this.updateBounds(currentBounds);
                    this.updateHeading(currentHeading);

                    if (t < 1) {
                        requestAnimationFrame(animate);
                    }
                };

                requestAnimationFrame(animate);
            }
        }

        // Firebase listener and overlay management
        const app = initializeApp(firebaseConfig);
        const db = getDatabase(app);
        const positionsRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

        const handleValueChange = (snapshot) => {
            if (snapshot.exists()) {
                const newPlanesMap = new Map();
                const newOverlays = new Map();

                snapshot.forEach((planeSnapshot) => {
                    const planeKey = planeSnapshot.key;
                    const lat = planeSnapshot.child('lat').val();
                    const lng = planeSnapshot.child('lng').val();
                    const heading = planeSnapshot.child('heading').val();
                    const position = new window.google.maps.LatLng(lat, lng);

                    newPlanesMap.set(planeKey, { lat, lng, heading, plane: planeKey });

                    const bounds = {
                        north: lat + 0.05,
                        south: lat - 0.05,
                        east: lng + 0.05,
                        west: lng - 0.05,
                    };

                    if (planeOverlays.has(planeKey)) {
                        // Update existing overlay
                        const overlay = planeOverlays.get(planeKey);
                        overlay.animateToPosition(bounds, heading);

                    } else {
                        // Create new overlay
                        const overlay = new CustomOverlay(bounds, planeImage, heading);
                        overlay.setMap(map);
                        newOverlays.set(planeKey, overlay);
                    }
                });

                // Remove overlays that are not in the new data
                planeOverlays.forEach((overlay, planeKey) => {
                    if (!newOverlays.has(planeKey)) {
                        overlay.remove();
                        overlay.setMap(null);
                        overlay.onRemove();
                    }
                });

                setPlanesMap(newPlanesMap);
                setPlaneOverlays(newOverlays);
            }
        };

        const unsubscribe = onValue(positionsRef, handleValueChange);

        // Cleanup the listener on unmount
        return () => {
            unsubscribe();
            planeOverlays.forEach(overlay => {
                overlay.setMap(null);
                overlay.onRemove();
            });
            setPlaneOverlays(new Map());
            setPlanesMap(new Map());
        };
    }, [map, isLoaded]);

    useEffect(() => {
        if (!map) return;

        const handleZoomChange = () => {
            const zoom = map.getZoom();
            const targetDimension = 0.1 * Math.pow(2, 10 - zoom); // Adjusted for smooth transition

            planeOverlays.forEach(overlay => {
                const bounds = overlay.bounds;
                const centerLat = (bounds.north + bounds.south) / 2;
                const centerLng = (bounds.east + bounds.west) / 2;

                const newBounds = {
                    north: centerLat + targetDimension / 2,
                    south: centerLat - targetDimension / 2,
                    east: centerLng + targetDimension / 2,
                    west: centerLng - targetDimension / 2,
                };

                overlay.animateToPosition(newBounds, overlay.heading);
            });
        };

        map.addListener('zoom_changed', handleZoomChange);

        return () => {
            window.google.maps.event.clearListeners(map, 'zoom_changed');
        };
    }, [map, planeOverlays]);

    return (
        <Fragment>
            <div className="outer-container">
                <div className="container">
                    <h1>Clear2Go-Map</h1>
                    <div style={{ height: "90vh", width: "140vh" }} ref={mapRef}></div>
                </div>
            </div>
        </Fragment>
    );
}

export default App;
