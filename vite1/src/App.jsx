import { Fragment, useEffect, useRef, useState } from "react";
import { useLoadScript } from "@react-google-maps/api";
import { initializeApp } from "firebase/app";
import { getDatabase, ref, onValue } from "firebase/database";
import "./App.css";
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
        googleMapsApiKey: import.meta.env.VITE_MAP_API_KEY,
    });
    const mapRef = useRef(null);
    const [map, setMap] = useState(null);
    const [planeOverlays, setPlaneOverlays] = useState({});

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
    }, [isLoaded]);

    useEffect(() => {
        if (!map) return;

        class CustomOverlay extends window.google.maps.OverlayView {
            constructor(bounds, image) {
                super();
                this.bounds = bounds;
                this.image = image;
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
                div.style.width = ne.x - sw.x + 'px';
                div.style.height = sw.y - ne.y + 'px';
            }

            onRemove() {
                if (this.div) {
                    this.div.parentNode.removeChild(this.div);
                    this.div = null;
                }
            }
        }

        const app = initializeApp(firebaseConfig);
        const db = getDatabase(app);
        const positionsRef = ref(db, 'Utilizare/Aviatie/Aerodromuri/AR_AT Bucuresti/Flota/Avioane');

        const handleValueChange = (snapshot) => {
            if (snapshot.exists()) {
                const newOverlays = {};

                // Remove existing overlays
                Object.values(planeOverlays).forEach(overlay => {
                    overlay.setMap(null);
                    overlay.onRemove();
                });

                snapshot.forEach((planeSnapshot) => {
                    const lat = planeSnapshot.child('lat').val();
                    const lng = planeSnapshot.child('lng').val();
                    const planeKey = planeSnapshot.key;

                    const bounds = {
                        north: lat + 0.05,
                        south: lat - 0.05,
                        east: lng + 0.05,
                        west: lng - 0.05,
                    };

                    const overlay = new CustomOverlay(bounds, planeImage);
                    overlay.setMap(map);
                    newOverlays[planeKey] = overlay;
                });

                setPlaneOverlays(newOverlays);
            }
        };

        const unsubscribe = onValue(positionsRef, handleValueChange);

        // Cleanup the listener on unmount
        return () => {
            unsubscribe();
            Object.values(planeOverlays).forEach(overlay => {
                overlay.setMap(null);
                overlay.onRemove();
            });
            setPlaneOverlays({});
        };
    }, [map]);

    return (
        <Fragment>
            <div className="container">
                <h1 className="text-center">Puii mei finally it exists</h1>
                <div style={{ height: "90vh", width: "100%" }} ref={mapRef}></div>
            </div>
        </Fragment>
    );
}

export default App;
