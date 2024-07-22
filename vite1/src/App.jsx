import React, { Fragment, useEffect, useRef, useState } from "react";
import { useLoadScript } from "@react-google-maps/api";
import { initializeApp } from "firebase/app";
import { getDatabase, ref, onValue, set } from "firebase/database";
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
    const [planeOverlays, setPlaneOverlays] = useState([]);
    const [isInputVisible, setIsInputVisible] = useState(false);
    const [inputValue, setInputValue] = useState("");

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
            constructor(bounds, image, heading, planeKey) {
                super();
                this.bounds = bounds;
                this.image = image;
                this.heading = heading;
                this.planeKey = planeKey;
                this.div = null;
            }

            onAdd() {
                const div = document.createElement('div');
                div.style.borderStyle = 'none';
                div.style.borderWidth = '0px';
                div.style.position = 'absolute';

                const label = document.createElement('div');
                label.innerText = this.planeKey;
                label.style.position = 'absolute';
                label.style.top = '-20px';
                label.style.left = '0';
                label.style.width = '100%';
                label.style.textAlign = 'center';
                label.style.color = 'black';
                label.style.fontWeight = 'bold';
                label.style.backgroundColor = 'white';

                const img = document.createElement('img');
                img.src = this.image;
                img.style.width = '100%';
                img.style.height = '100%';
                img.style.position = 'absolute';
                img.style.visibility = 'visible';
                img.style.transform = `rotate(${this.heading}deg)`;
                img.style.transition = 'transform 0.5s, width 0.5s, height 0.5s'; // Add transition

                div.appendChild(label);
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
        const messageRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Mesaj aeronave');

        const calculateOverlayDimensions = (zoom) => {
            // Adjust this factor to change the size of the overlays based on zoom level
            const baseDimension = 0.05; // Base dimension at zoom level 10
            const scale = Math.pow(2, 10 - zoom);
            return baseDimension * scale;
        };

        const updateOverlays = (snapshot, zoom) => {
            if (snapshot.exists()) {
                const newOverlays = [];

                snapshot.forEach((planeSnapshot) => {
                    const planeKey = planeSnapshot.key;
                    const lat = planeSnapshot.child('lat').val();
                    const lng = planeSnapshot.child('lng').val();
                    const heading = planeSnapshot.child('heading').val();

                    const dimension = calculateOverlayDimensions(zoom);
                    const bounds = {
                        north: lat + dimension,
                        south: lat - dimension,
                        east: lng + dimension,
                        west: lng - dimension,
                    };

                    // Create new overlay
                    const overlay = new CustomOverlay(bounds, planeImage, heading, planeKey);
                    overlay.setMap(map);
                    newOverlays.push(overlay);
                });

                // Remove old overlays
                planeOverlays.forEach((overlay) => {
                    overlay.setMap(null);
                    overlay.onRemove();
                });

                // Update state with new overlays
                setPlaneOverlays(newOverlays);
            }
        };

        const fetchAndUpdateData = () => {
            const zoom = map.getZoom();
            onValue(positionsRef, (snapshot) => updateOverlays(snapshot, zoom), {
                onlyOnce: true,
            });
        };

        const intervalId = setInterval(fetchAndUpdateData, 1000); // Fetch data every second

        // Cleanup the interval on unmount
        return () => {
            clearInterval(intervalId);
        };
    }, [map, isLoaded, planeOverlays]);

    useEffect(() => {
        if (!map) return;

        const handleZoomChange = () => {
            fetchAndUpdateData();
        };

        map.addListener('zoom_changed', handleZoomChange);

        return () => {
            window.google.maps.event.clearListeners(map, 'zoom_changed');
        };
    }, [map]);

    useEffect(() => {
        if (!isLoaded) return;

        const app = initializeApp(firebaseConfig);
        const db = getDatabase(app);
        const messageRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Mesaj aeronave');

        onValue(messageRef, (snapshot) => {
            if (snapshot.exists()) {
                setInputValue(snapshot.val());
            }
        });
    }, [isLoaded]);

    const handleButtonClick = () => {
        setIsInputVisible(!isInputVisible);
    };

    const handleInputChange = (event) => {
        const app = initializeApp(firebaseConfig);
        const db = getDatabase(app);
        const messageRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Mesaj aeronave');
        const newValue = event.target.value;
        set(messageRef, newValue);
        setInputValue(newValue);

        // Update Firebase with new input value


    };

    return (
        <Fragment>
            <div className="outer-container">
                <div className="container">
                    <h1>Clear2Go-Map</h1>
                    <div style={{ height: "90vh", width: "140vh" }} ref={mapRef}></div>
                    <button
                        style={{
                            position: "absolute",
                            bottom: "20px",
                            right: "20px",
                            padding: "10px 20px",
                            borderRadius: "5px",
                            border: "none",
                            backgroundColor: "#007bff",
                            color: "white",
                            cursor: "pointer",
                        }}
                        onClick={handleButtonClick}
                    >
                        {isInputVisible ? "Inchide notam" : "Deschide notam"}
                    </button>
                    {isInputVisible && (
                        <input
                            type="text"
                            value={inputValue}
                            onChange={handleInputChange}
                            style={{
                                position: "absolute",
                                bottom: "70px",
                                right: "20px",
                                padding: "10px",
                                borderRadius: "5px",
                                border: "1px solid #ccc",
                                width: "200px",
                            }}
                        />
                    )}
                </div>
            </div>
        </Fragment>
    );
}

export default App;
