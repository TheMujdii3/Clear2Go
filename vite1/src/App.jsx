import  { Fragment, useEffect, useRef, useState } from "react";
import {
    GoogleMap,
    useLoadScript,
} from "@react-google-maps/api";
import "./App.css";
import planeImage from './plane2.png'; // Import your local image

const customOverlayData = {
    bounds: {
        north: 44.1,
        south: 44.0,
        east: 25.7,
        west: 25.6,
    },
    image: planeImage,
};

function App() {
    const { isLoaded } = useLoadScript({
        googleMapsApiKey: import.meta.env.VITE_MAP_API_KEY,
    });

    const mapRef = useRef(null);
    const [map, setMap] = useState(null);

    useEffect(() => {
        if (!mapRef.current) return;

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
    }, [mapRef]);

    useEffect(() => {
        if (!map) return;

        const CustomOverlay = function(bounds, image) {
            this.bounds = bounds;
            this.image = image;
            this.div = null;
        };

        CustomOverlay.prototype = new window.google.maps.OverlayView();

        CustomOverlay.prototype.onAdd = function() {
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
        };

        CustomOverlay.prototype.draw = function() {
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
        };

        CustomOverlay.prototype.onRemove = function() {
            if (this.div) {
                this.div.parentNode.removeChild(this.div);
                this.div = null;
            }
        };

        const bounds = customOverlayData.bounds;
        const image = customOverlayData.image;
        const overlay = new CustomOverlay(bounds, image);
        overlay.setMap(map);
    }, [map]);

    return (
        <Fragment>
            <div className="container">
                <h1 className="text-center">Puii mei finally it existdawawdawdawdaw</h1>
                <div style={{ height: "90vh", width: "100%" }} ref={mapRef}></div>
            </div>
        </Fragment>
    );
}

export default App;
