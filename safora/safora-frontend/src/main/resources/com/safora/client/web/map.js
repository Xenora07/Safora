var map;
var currentMarker = null;
var markers = [];
var routePolyline = null;

function initMap() {
    // FIX: JavaFX WebView on Mac Retina has a bug with 3D CSS transforms (translate3d)
    // which causes Leaflet tiles to render scattered with large gaps.
    // By forcing any3d to false, Leaflet falls back to standard top/left CSS positioning.
    L.Browser.any3d = false;

    map = L.map('map', {
        zoomControl: false, // Hide default zoom, we can add it custom or rely on scroll
        zoomSnap: 0.5,
        zoomDelta: 0.5,
        zoomAnimation: true,
        markerZoomAnimation: true,
        fadeAnimation: true,
        wheelPxPerZoomLevel: 100,
        inertia: true,
        inertiaDeceleration: 2000,
        inertiaMaxSpeed: 1000
    }).setView([19.0441, 72.9103], 13); // Default to Mumbai

    L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '&copy; OpenStreetMap contributors &copy; CARTO',
        subdomains: 'abcd',
        maxZoom: 20
    }).addTo(map);
    
    // Toggle tooltips based on zoom
    map.on('zoomend', function() {
        var currentZoom = map.getZoom();
        if (currentZoom >= 15) {
            document.querySelectorAll('.leaflet-tooltip').forEach(el => el.style.opacity = '1');
        } else {
            document.querySelectorAll('.leaflet-tooltip').forEach(el => el.style.opacity = '0');
        }
    });
    
    // Robust resize handling using ResizeObserver
    if (typeof ResizeObserver !== 'undefined') {
        new ResizeObserver(function() {
            if (map) {
                map.invalidateSize();
            }
        }).observe(document.getElementById('map'));
    } else {
        // Fallback to window resize
        window.addEventListener('resize', function() {
            if (map) {
                map.invalidateSize();
            }
        });
    }
    
    if (typeof javaBridge !== 'undefined') {
        javaBridge.log("Map initialized successfully");
    }
    
    // Explicitly invalidate size continuously for the first 2 seconds to catch late JavaFX layout passes
    let count = 0;
    let initInterval = setInterval(function() {
        if (map) {
            map.invalidateSize(true);
        }
        count++;
        if (count > 20) {
            clearInterval(initInterval);
        }
    }, 100);
}

function setCenter(lat, lng) {
    if (map) {
        map.invalidateSize();
        map.setView([lat, lng], 15);
    }
}

function setCurrentLocation(lat, lng) {
    if (!map) return;
    map.invalidateSize();
    
    if (currentMarker) {
        currentMarker.setLatLng([lat, lng]);
    } else {
        var icon = L.divIcon({
            className: 'custom-marker',
            iconSize: [20, 20],
            iconAnchor: [10, 10]
        });
        currentMarker = L.marker([lat, lng], {icon: icon}).addTo(map);
    }
}

function addMarker(lat, lng, type) {
    if (!map) return;
    map.invalidateSize();
    
    var color = '#EF4444'; // default red for danger
    if (type === 'destination') color = '#10B981'; // green
    if (type === 'warning') color = '#F59E0B'; // amber
    
    var icon = L.divIcon({
        className: 'custom-marker',
        html: `<div style="background-color: ${color}; width: 100%; height: 100%; border-radius: 50%; border: 2px solid white; box-shadow: 0 0 4px rgba(0,0,0,0.4);"></div>`,
        iconSize: [24, 24],
        iconAnchor: [12, 12]
    });
    
    var popupContent = `
        <div class="popup-type">${type.toUpperCase()}</div>
        <div class="popup-title">Checkpoint</div>
        <div class="popup-desc">Information unavailable</div>
    `;
    
    var m = L.marker([lat, lng], {icon: icon})
        .bindPopup(popupContent, {closeButton: false})
        .addTo(map);
    markers.push(m);
}

function addCheckpointMarker(lat, lng, type, title, desc) {
    if (!map) return;
    
    let emoji = "📍";
    let bgColor = "#FFF";
    let animClass = "";
    
    if (type === "current") {
        emoji = "📍";
        bgColor = "#DBEAFE"; // light blue
        animClass = "pulsing-dot";
    } else if (type === "destination") {
        emoji = "🎯";
        bgColor = "#FECACA"; // light red
        animClass = "bounce-drop";
    } else if (type === "police") {
        emoji = "🚓";
        bgColor = "#DBEAFE"; // light blue
    } else if (type === "hospital") {
        emoji = "🏥";
        bgColor = "#FCE7F3"; // light pink
    } else if (type === "lighting") {
        emoji = "💡";
        bgColor = "#FEF3C7"; // light yellow
        animClass = "soft-glow";
    }

    var icon = L.divIcon({
        className: 'checkpoint-marker ' + animClass,
        html: `<div style="background-color: ${bgColor}; width: 34px; height: 34px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 18px; border: 2px solid white; box-shadow: 0 2px 4px rgba(0,0,0,0.2);">${emoji}</div>`,
        iconSize: [34, 34],
        iconAnchor: [17, 17]
    });
    
    var popupContent = `
        <div class="popup-type">${type}</div>
        <div class="popup-title">${title}</div>
        <div class="popup-desc">${desc}</div>
    `;
    
    var m = L.marker([lat, lng], {icon: icon})
        .bindPopup(popupContent, {closeButton: false, offset: [0, -10]})
        .bindTooltip(title, {
            permanent: true,
            direction: 'right',
            className: 'checkpoint-label',
            offset: [15, 0]
        })
        .addTo(map);
        
    // Apply zoom visibility rule immediately
    if (map.getZoom() < 15) {
        var tooltipNode = m.getTooltip()._container;
        if (tooltipNode) tooltipNode.style.opacity = '0';
    }
        
    markers.push(m);
}

function drawRoute(pointsJson, color) {
    if (!map) return;
    map.invalidateSize();
    
    // Clear existing
    if (routePolyline) {
        map.removeLayer(routePolyline);
    }
    
    try {
        var points = JSON.parse(pointsJson);
        var routeColor = color || '#2563EB'; // default to blue
        routePolyline = L.polyline(points, {
            color: routeColor,
            weight: 7,
            opacity: 0.9,
            smoothFactor: 1.5,
            className: 'route-glow'
        }).addTo(map);
        
        map.fitBounds(routePolyline.getBounds(), {padding: [80, 80], maxZoom: 16});
    } catch(e) {
        if (typeof javaBridge !== 'undefined') {
            javaBridge.log("Error parsing route JSON: " + e.message);
        }
    }
}

function clearMarkers() {
    markers.forEach(m => map.removeLayer(m));
    markers = [];
}
