// GoogleMapsLoader.js

import { Loader } from '@googlemaps/js-api-loader';

let googleMapsLoader = null;

const getGoogleMapsLoader = () => {
  if (!googleMapsLoader) {
    googleMapsLoader = new Loader({
      apiKey: 'YOUR_GOOGLE_MAPS_API_KEY', // Replace with your actual API key
      version: 'weekly',
      id: '__googleMapsScriptId', // Unique identifier for the script
    });
  }

  return googleMapsLoader;
};

export default getGoogleMapsLoader;
