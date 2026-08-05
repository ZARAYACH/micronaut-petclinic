@import io.micronaut.samples.petclinic.dto.ClinicDto
@import java.util.List

@param List<ClinicDto> clinics
@param String mode
@param double latitude
@param double longitude
@param double radiusMeters
@param double minLatitude
@param double minLongitude
@param double maxLatitude
@param double maxLongitude
@param io.micronaut.samples.petclinic.system.I18nViewModelProcessor.Msg msg
@param io.micronaut.http.HttpRequest<?> request
@param java.util.Locale locale

@template.layout(content = @`
    <link href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" rel="stylesheet">
    <link href="https://unpkg.com/@@geoman-io/leaflet-geoman-free@2.18.3/dist/leaflet-geoman.css" rel="stylesheet">
    <style>
        .clinic-map {
            height: 420px;
            min-height: 420px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            overflow: hidden;
        }
    </style>

    <div class="page-header">
        <div class="container">
            <h1><i class="fas fa-location-dot me-3"></i>${msg.text("clinic.findNearby")}</h1>
            <p>${msg.text("clinic.spatialIntro")}</p>
        </div>
    </div>

    <main class="container flex-grow-1 py-4">
        <div class="search-box mb-4 animate-fade-in-up">
            <form action="/clinics" method="get" id="clinic-search-form" novalidate>
                <div class="row g-3 mb-3">
                    <div class="col-md-4">
                        <label for="mode" class="form-label">
                            <i class="fas fa-compass me-2"></i>${msg.text("clinic.searchMode")}
                        </label>
                        <select class="form-select form-select-lg" id="mode" name="mode">
                            <option value="near" selected="${"near".equals(mode)}">${msg.text("clinic.mode.near")}</option>
                            <option value="within" selected="${"within".equals(mode)}">${msg.text("clinic.mode.within")}</option>
                            <option value="intersects" selected="${"intersects".equals(mode)}">${msg.text("clinic.mode.intersects")}</option>
                        </select>
                    </div>
                </div>

                <ul class="nav nav-tabs mb-3" id="clinic-search-tabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="manual-search-tab" type="button" role="tab"
                                data-bs-toggle="tab" data-bs-target="#manual-search-panel"
                                aria-controls="manual-search-panel" aria-selected="true">
                            <i class="fas fa-keyboard me-2"></i>${msg.text("clinic.searchTab.manual")}
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="map-search-tab" type="button" role="tab"
                                data-bs-toggle="tab" data-bs-target="#map-search-panel"
                                aria-controls="map-search-panel" aria-selected="false">
                            <i class="fas fa-map me-2"></i>${msg.text("clinic.searchTab.map")}
                        </button>
                    </li>
                </ul>

                <div class="tab-content">
                    <div class="tab-pane fade show active" id="manual-search-panel" role="tabpanel"
                         aria-labelledby="manual-search-tab">
                        <div class="row g-3">
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="latitude" class="form-label">
                                    <i class="fas fa-map-pin me-2"></i>${msg.text("clinic.latitude")}
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="latitude"
                                       name="latitude" value="${latitude}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="longitude" class="form-label">
                                    <i class="fas fa-map-pin me-2"></i>${msg.text("clinic.longitude")}
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="longitude"
                                       name="longitude" value="${longitude}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="radiusMeters" class="form-label">
                                    <i class="fas fa-ruler-combined me-2"></i>${msg.text("clinic.radiusMeters")}
                                </label>
                                <input type="number" step="100" min="100" class="form-control form-control-lg" id="radiusMeters"
                                       name="radiusMeters" value="${radiusMeters}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="minLatitude" class="form-label">${msg.text("clinic.minLatitude")}</label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="minLatitude"
                                       name="minLatitude" value="${minLatitude}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="minLongitude" class="form-label">${msg.text("clinic.minLongitude")}</label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="minLongitude"
                                       name="minLongitude" value="${minLongitude}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="maxLatitude" class="form-label">${msg.text("clinic.maxLatitude")}</label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="maxLatitude"
                                       name="maxLatitude" value="${maxLatitude}">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="maxLongitude" class="form-label">${msg.text("clinic.maxLongitude")}</label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="maxLongitude"
                                       name="maxLongitude" value="${maxLongitude}">
                            </div>
                            <div class="col-md-4 d-flex align-items-end">
                                <button class="btn btn-primary btn-lg w-100" type="submit">
                                    <i class="fas fa-search me-2"></i>${msg.text("button.search")}
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane fade" id="map-search-panel" role="tabpanel" aria-labelledby="map-search-tab">
                        <div id="clinic-map" class="clinic-map"></div>
                        <div class="mt-3 d-flex justify-content-end gap-2">
                            <button class="btn btn-secondary btn-lg" type="button" id="clinic-clear-drawing">
                                <i class="fas fa-eraser me-2"></i>Clear Drawing
                            </button>
                            <button class="btn btn-primary btn-lg" type="submit">
                                <i class="fas fa-search me-2"></i>${msg.text("button.search")}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="mb-1" id="clinic-results-title"
                    data-template="${msg.text("clinic.resultsTitle", "__mode__")}">${msg.text("clinic.resultsTitle", msg.text("clinic.mode." + mode))}</h2>
                <p class="text-muted mb-0" id="clinic-results-count"
                   data-template="${msg.text("clinic.resultsCount", "__count__")}">${msg.text("clinic.resultsCount", clinics != null ? clinics.size() : 0)}</p>
            </div>
            <a href="/clinics" class="btn btn-secondary">
                <i class="fas fa-rotate-left me-2"></i>${msg.text("clinic.resetSearch")}
            </a>
        </div>

        <div id="clinic-results"
             data-empty-title="${msg.text("clinic.noClinicsFound")}"
             data-empty-message="${msg.text("clinic.noClinicsMessage")}"
             data-coordinates-label="${msg.text("clinic.coordinates")}">
            @if(clinics == null || clinics.isEmpty())
            <div class="empty-state" id="clinic-empty-state">
                <div class="empty-state-icon">
                    <i class="fas fa-map-location"></i>
                </div>
                <h3>${msg.text("clinic.noClinicsFound")}</h3>
                <p>${msg.text("clinic.noClinicsMessage")}</p>
            </div>
            @else
            <div class="row g-4" id="clinic-results-list">
                @for(ClinicDto clinic : clinics)
                    <div class="col-md-6 col-lg-4">
                        <div class="owner-card animate-fade-in-up">
                            <div class="d-flex align-items-start mb-3">
                                <div class="owner-avatar me-3">
                                    <i class="fas fa-hospital"></i>
                                </div>
                                <div class="flex-grow-1">
                                    <h5 class="mb-1">${clinic.name()}</h5>
                                    <p class="text-muted mb-0 small">
                                        <i class="fas fa-city me-1"></i>${clinic.city()}
                                    </p>
                                </div>
                            </div>

                            <div class="mb-3">
                                <p class="mb-1 small text-muted">
                                    <i class="fas fa-home me-2"></i>${clinic.address()}
                                </p>
                                <p class="mb-0 small text-muted">
                                    <i class="fas fa-crosshairs me-2"></i>
                                    ${msg.text("clinic.coordinates")}: ${clinic.latitude()}, ${clinic.longitude()}
                                </p>
                            </div>

                            <span class="pet-badge">
                                <i class="fas fa-circle-check"></i>
                                ${msg.text("clinic.mode." + mode)}
                            </span>
                        </div>
                    </div>
                @endfor
            </div>
            @endif
        </div>
    </main>

    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
    <script src="https://unpkg.com/@@geoman-io/leaflet-geoman-free@2.18.3/dist/leaflet-geoman.min.js"></script>
    <script>
        (function () {
            const form = document.getElementById('clinic-search-form');
            const modeSelect = document.getElementById('mode');
            const mapTab = document.getElementById('map-search-tab');
            const mapPanel = document.getElementById('map-search-panel');
            const mapContainer = document.getElementById('clinic-map');
            const clearDrawingButton = document.getElementById('clinic-clear-drawing');
            const results = document.getElementById('clinic-results');
            const resultsTitle = document.getElementById('clinic-results-title');
            const resultsCount = document.getElementById('clinic-results-count');
            if (!form || !modeSelect || !results || !resultsTitle || !resultsCount) {
                return;
            }

            const nearFields = document.querySelectorAll('.clinic-field-near');
            const boundsFields = document.querySelectorAll('.clinic-field-bounds');
            const mapBounds = [[42.45, -92.95], [47.10, -86.75]];
            let clinicMap = null;
            let referenceMarker = null;
            let radiusCircle = null;
            let radiusHandle = null;
            let drawnLayer = null;
            let drawnPoints = [];
            let draftPointMarkers = null;
            let resultMarkers = null;
            let clinicMarkerIcon = null;

            function toggleGroup(fields, hidden) {
                fields.forEach((field) => {
                    field.classList.toggle('d-none', hidden);
                    field.querySelectorAll('input').forEach((input) => {
                        input.disabled = hidden;
                    });
                });
            }

            function applyMode(mode) {
                const isNear = mode === 'near';
                toggleGroup(nearFields, !isNear);
                toggleGroup(boundsFields, isNear);
                clearDrawing();
                applyMapMode(mode);
            }

            function fieldValue(name) {
                return form.elements[name].value;
            }

            function setFieldValue(name, value) {
                form.elements[name].value = value;
            }

            function numberFieldValue(name) {
                return Number(fieldValue(name));
            }

            function searchUrl(mode) {
                const params = new URLSearchParams();
                if (mode === 'near') {
                    params.set('latitude', fieldValue('latitude'));
                    params.set('longitude', fieldValue('longitude'));
                    params.set('radiusMeters', fieldValue('radiusMeters'));
                    return '/clinics/nearby?' + params.toString();
                }
                params.set('minLatitude', fieldValue('minLatitude'));
                params.set('minLongitude', fieldValue('minLongitude'));
                params.set('maxLatitude', fieldValue('maxLatitude'));
                params.set('maxLongitude', fieldValue('maxLongitude'));
                return '/clinics/' + mode + '?' + params.toString();
            }

            function searchRequest(mode) {
                if (isMapTabActive() && (mode === 'within' || mode === 'intersects') && drawnLayer) {
                    return {
                        url: '/clinics/' + mode,
                        options: {
                            method: 'POST',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                coordinates: drawnLayerCoordinates()
                            })
                        }
                    };
                }
                return {
                    url: searchUrl(mode),
                    options: {
                        headers: {
                            'Accept': 'application/json'
                        }
                    }
                };
            }

            function escapeHtml(value) {
                return String(value).replace(/[&<>"']/g, function (character) {
                    return {
                        '&': '&amp;',
                        '<': '&lt;',
                        '>': '&gt;',
                        '"': '&quot;',
                        "'": '&#39;'
                    }[character];
                });
            }

            function selectedModeLabel() {
                return modeSelect.options[modeSelect.selectedIndex].text;
            }

            function updateSummary(modeLabel, count) {
                resultsTitle.textContent = resultsTitle.dataset.template.replace('__mode__', modeLabel);
                resultsCount.textContent = resultsCount.dataset.template.replace('__count__', count);
            }

            function renderEmptyState() {
                renderMapMarkers([]);
                results.innerHTML = [
                    '<div class="empty-state" id="clinic-empty-state">',
                    '<div class="empty-state-icon">',
                    '<i class="fas fa-map-location"></i>',
                    '</div>',
                    '<h3>' + escapeHtml(results.dataset.emptyTitle) + '</h3>',
                    '<p>' + escapeHtml(results.dataset.emptyMessage) + '</p>',
                    '</div>'
                ].join('');
            }

            function renderClinics(clinics, modeLabel) {
                if (clinics.length === 0) {
                    renderEmptyState();
                    return;
                }
                results.innerHTML = '<div class="row g-4" id="clinic-results-list">' +
                    clinics.map(function (clinic) {
                        return [
                            '<div class="col-md-6 col-lg-4">',
                            '<div class="owner-card animate-fade-in-up">',
                            '<div class="d-flex align-items-start mb-3">',
                            '<div class="owner-avatar me-3">',
                            '<i class="fas fa-hospital"></i>',
                            '</div>',
                            '<div class="flex-grow-1">',
                            '<h5 class="mb-1">' + escapeHtml(clinic.name) + '</h5>',
                            '<p class="text-muted mb-0 small">',
                            '<i class="fas fa-city me-1"></i>' + escapeHtml(clinic.city),
                            '</p>',
                            '</div>',
                            '</div>',
                            '<div class="mb-3">',
                            '<p class="mb-1 small text-muted">',
                            '<i class="fas fa-home me-2"></i>' + escapeHtml(clinic.address),
                            '</p>',
                            '<p class="mb-0 small text-muted">',
                            '<i class="fas fa-crosshairs me-2"></i>',
                            escapeHtml(results.dataset.coordinatesLabel) + ': ' + escapeHtml(clinic.latitude) + ', ' + escapeHtml(clinic.longitude),
                            '</p>',
                            '</div>',
                            '<span class="pet-badge">',
                            '<i class="fas fa-circle-check"></i>',
                            escapeHtml(modeLabel),
                            '</span>',
                            '</div>',
                            '</div>'
                        ].join('');
                    }).join('') +
                    '</div>';
                renderMapMarkers(clinics);
            }

            function isMapTabActive() {
                return mapPanel && mapPanel.classList.contains('active');
            }

            function initializeMap() {
                if (clinicMap || typeof L === 'undefined' || !mapContainer) {
                    return;
                }

                clinicMap = L.map('clinic-map', {
                    maxBounds: mapBounds,
                    maxBoundsViscosity: 1.0,
                    minZoom: 6
                }).setView([numberFieldValue('latitude'), numberFieldValue('longitude')], 7);
                L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19,
                    attribution: '&copy; OpenStreetMap contributors'
                }).addTo(clinicMap);
                clinicMarkerIcon = L.icon({
                    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
                    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
                    iconSize: [25, 41],
                    iconAnchor: [12, 41],
                    popupAnchor: [1, -34],
                    shadowSize: [41, 41]
                });

                resultMarkers = L.featureGroup().addTo(clinicMap);
                draftPointMarkers = L.layerGroup().addTo(clinicMap);
                referenceMarker = L.marker([numberFieldValue('latitude'), numberFieldValue('longitude')], {
                    draggable: true
                }).addTo(clinicMap);
                radiusCircle = L.circle(referenceMarker.getLatLng(), {
                    radius: numberFieldValue('radiusMeters'),
                    color: '#0d6efd',
                    fillColor: '#0d6efd',
                    fillOpacity: 0.12
                }).addTo(clinicMap);
                radiusHandle = L.marker(radiusHandleLatLng(), {
                    draggable: true,
                    title: 'Drag to resize radius'
                }).addTo(clinicMap);

                if (clinicMap.pm) {
                    clinicMap.on('pm:create', function (event) {
                        setDrawnLayer(event.layer);
                    });
                }

                referenceMarker.on('dragend', function () {
                    setNearPoint(referenceMarker.getLatLng());
                });
                radiusHandle.on('drag', syncRadiusFromHandle);
                radiusHandle.on('dragend', syncRadiusFromHandle);
                clinicMap.on('click', function (event) {
                    if (modeSelect.value === 'near') {
                        setNearPoint(event.latlng);
                    } else {
                        appendDrawPoint(event.latlng);
                    }
                });

                updateBoundsFromInputs();
                applyMapMode(modeSelect.value);
                setTimeout(function () {
                    clinicMap.invalidateSize();
                }, 150);
            }

            function applyMapMode(mode) {
                if (!clinicMap || !referenceMarker || !radiusCircle || !radiusHandle) {
                    return;
                }
                const isNear = mode === 'near';
                updateDrawControls(mode);
                if (isNear) {
                    if (!clinicMap.hasLayer(referenceMarker)) {
                        referenceMarker.addTo(clinicMap);
                    }
                    if (!clinicMap.hasLayer(radiusCircle)) {
                        radiusCircle.addTo(clinicMap);
                    }
                    if (!clinicMap.hasLayer(radiusHandle)) {
                        radiusHandle.addTo(clinicMap);
                    }
                    updateRadiusHandle();
                } else {
                    if (clinicMap.hasLayer(referenceMarker)) {
                        clinicMap.removeLayer(referenceMarker);
                    }
                    if (clinicMap.hasLayer(radiusCircle)) {
                        clinicMap.removeLayer(radiusCircle);
                    }
                    if (clinicMap.hasLayer(radiusHandle)) {
                        clinicMap.removeLayer(radiusHandle);
                    }
                    updateBoundsFromInputs();
                }
            }

            function updateDrawControls(mode) {
                if (!clinicMap || !clinicMap.pm) {
                    return;
                }
                if (typeof clinicMap.pm.removeControls === 'function') {
                    clinicMap.pm.removeControls();
                }
                if (mode !== 'near' && typeof clinicMap.pm.addControls === 'function') {
                    clinicMap.pm.addControls({
                        position: 'topleft',
                        drawPolygon: true,
                        drawRectangle: true,
                        drawMarker: false,
                        drawCircle: false,
                        drawCircleMarker: false,
                        drawPolyline: mode === 'intersects',
                        drawText: false,
                        editMode: true,
                        dragMode: true,
                        removalMode: true,
                        cutPolygon: false,
                        rotateMode: false
                    });
                }
            }

            function setNearPoint(latlng) {
                const normalized = clinicMap.wrapLatLng(L.latLng(latlng.lat, latlng.lng));
                referenceMarker.setLatLng(normalized);
                radiusCircle.setLatLng(normalized);
                setFieldValue('latitude', normalized.lat.toFixed(4));
                setFieldValue('longitude', normalized.lng.toFixed(4));
                updateRadiusHandle();
            }

            function radiusHandleLatLng() {
                const center = referenceMarker.getLatLng();
                const radiusMeters = numberFieldValue('radiusMeters');
                const longitudeOffset = radiusMeters / (111320 * Math.cos(center.lat * Math.PI / 180));
                return L.latLng(center.lat, center.lng + longitudeOffset);
            }

            function updateRadiusHandle() {
                if (radiusHandle) {
                    radiusHandle.setLatLng(radiusHandleLatLng());
                }
            }

            function syncRadiusFromHandle() {
                const radiusMeters = Math.max(100, Math.round(referenceMarker.getLatLng().distanceTo(radiusHandle.getLatLng())));
                radiusCircle.setRadius(radiusMeters);
                setFieldValue('radiusMeters', radiusMeters);
            }

            function setDrawnLayer(layer) {
                if (drawnLayer && clinicMap.hasLayer(drawnLayer)) {
                    clinicMap.removeLayer(drawnLayer);
                }
                if (clinicMap.hasLayer(layer)) {
                    clinicMap.removeLayer(layer);
                }
                drawnPoints = layerLatLngs(layer);
                renderDraftPointMarkers();
                renderDrawnLayerFromPoints();
            }

            function syncDrawnLayer(refreshMarkers) {
                if (!drawnLayer || !drawnLayer.getBounds) {
                    return;
                }
                if (refreshMarkers !== false) {
                    drawnPoints = layerLatLngs(drawnLayer);
                    renderDraftPointMarkers();
                    if (modeSelect.value === 'intersects' && drawnPoints.length >= 3) {
                        renderDrawnLayerFromPoints();
                        return;
                    }
                }
                const bounds = drawnLayer.getBounds();
                setFieldValue('minLatitude', bounds.getSouth().toFixed(4));
                setFieldValue('minLongitude', bounds.getWest().toFixed(4));
                setFieldValue('maxLatitude', bounds.getNorth().toFixed(4));
                setFieldValue('maxLongitude', bounds.getEast().toFixed(4));
            }

            function updateBoundsFromInputs() {
                if (!clinicMap || drawnLayer) {
                    return;
                }
                const southWest = [numberFieldValue('minLatitude'), numberFieldValue('minLongitude')];
                const northEast = [numberFieldValue('maxLatitude'), numberFieldValue('maxLongitude')];
                drawnLayer = L.rectangle([southWest, northEast], {
                    color: '#198754',
                    fillOpacity: 0.08
                }).addTo(clinicMap);
                drawnPoints = layerLatLngs(drawnLayer);
                renderDraftPointMarkers();
                enableDrawnLayerEditing();
            }

            function appendDrawPoint(latlng) {
                if (drawnPoints.length === 0 && drawnLayer && clinicMap.hasLayer(drawnLayer)) {
                    clinicMap.removeLayer(drawnLayer);
                    drawnLayer = null;
                }
                drawnPoints.push(L.latLng(latlng.lat, latlng.lng));
                renderDraftPointMarkers();
                renderDrawnLayerFromPoints();
            }

            function renderDrawnLayerFromPoints() {
                if (drawnLayer && clinicMap.hasLayer(drawnLayer)) {
                    clinicMap.removeLayer(drawnLayer);
                }
                if (drawnPoints.length < 2) {
                    drawnLayer = null;
                    return;
                }
                if (modeSelect.value === 'within' && drawnPoints.length >= 3) {
                    drawnLayer = L.polygon(drawnPoints, {
                        color: '#198754',
                        fillOpacity: 0.12
                    });
                } else {
                    drawnLayer = L.polyline(linePointsForMode(), {
                        color: modeSelect.value === 'intersects' ? '#dc3545' : '#198754',
                        weight: 3
                    });
                }
                drawnLayer.addTo(clinicMap);
                enableDrawnLayerEditing();
                syncDrawnLayer(false);
            }

            function enableDrawnLayerEditing() {
                if (drawnLayer && drawnLayer.pm) {
                    drawnLayer.pm.enable();
                    drawnLayer.on('pm:edit', syncDrawnLayer);
                    drawnLayer.on('pm:update', syncDrawnLayer);
                }
            }

            function linePointsForMode() {
                if (modeSelect.value === 'intersects' && drawnPoints.length >= 3) {
                    return drawnPoints.concat([drawnPoints[0]]);
                }
                return drawnPoints;
            }

            function renderDraftPointMarkers() {
                if (!draftPointMarkers) {
                    return;
                }
                draftPointMarkers.clearLayers();
                drawnPoints.forEach(function (point, index) {
                    const marker = L.marker(point, {
                        draggable: true,
                        title: 'Drag to move point'
                    });
                    marker.on('drag', function () {
                        drawnPoints[index] = marker.getLatLng();
                        renderDrawnLayerFromPoints();
                    });
                    marker.on('dragend', function () {
                        drawnPoints[index] = marker.getLatLng();
                        renderDrawnLayerFromPoints();
                    });
                    marker.addTo(draftPointMarkers);
                });
            }

            function clearDrawing() {
                drawnPoints = [];
                if (drawnLayer && clinicMap && clinicMap.hasLayer(drawnLayer)) {
                    clinicMap.removeLayer(drawnLayer);
                }
                drawnLayer = null;
                if (draftPointMarkers) {
                    draftPointMarkers.clearLayers();
                }
            }

            function layerLatLngs(layer) {
                if (!layer || !layer.getLatLngs) {
                    return [];
                }
                const latLngs = layer.getLatLngs();
                if (latLngs.length > 0 && Array.isArray(latLngs[0])) {
                    return normalizeLayerPoints(latLngs[0]);
                }
                return normalizeLayerPoints(latLngs);
            }

            function normalizeLayerPoints(points) {
                if (modeSelect.value === 'intersects' && drawnPoints.length >= 3 && points.length === drawnPoints.length + 1) {
                    return points.slice(0, points.length - 1);
                }
                return removeClosingPoint(points);
            }

            function removeClosingPoint(points) {
                if (points.length < 2) {
                    return points;
                }
                const first = points[0];
                const last = points[points.length - 1];
                if (first.lat === last.lat && first.lng === last.lng) {
                    return points.slice(0, points.length - 1);
                }
                return points;
            }

            function drawnLayerCoordinates() {
                if (!drawnLayer) {
                    return [];
                }
                const points = modeSelect.value === 'intersects' ? linePointsForMode() : closedPointsForMode();
                return points.map(function (point) {
                    return {
                        longitude: point.lng,
                        latitude: point.lat
                    };
                });
            }

            function closedPointsForMode() {
                if (drawnPoints.length >= 3) {
                    return drawnPoints.concat([drawnPoints[0]]);
                }
                return drawnPoints;
            }

            function renderMapMarkers(clinics) {
                if (!clinicMap || !resultMarkers) {
                    return;
                }
                resultMarkers.clearLayers();
                clinics.forEach(function (clinic) {
                    L.marker([clinic.latitude, clinic.longitude], {
                        icon: clinicMarkerIcon
                    })
                        .bindPopup(escapeHtml(clinic.name))
                        .addTo(resultMarkers);
                });
                if (clinics.length > 0) {
                    clinicMap.fitBounds(resultMarkers.getBounds().pad(0.2), {
                        maxZoom: 13
                    });
                }
            }

            modeSelect.addEventListener('change', function () {
                applyMode(this.value);
            });
            if (mapTab) {
                mapTab.addEventListener('click', function () {
                    setTimeout(initializeMap, 150);
                });
            }
            if (clearDrawingButton) {
                clearDrawingButton.addEventListener('click', clearDrawing);
            }
            ['latitude', 'longitude'].forEach(function (name) {
                form.elements[name].addEventListener('input', function () {
                    if (clinicMap && referenceMarker && radiusCircle) {
                        setNearPoint({
                            lat: numberFieldValue('latitude'),
                            lng: numberFieldValue('longitude')
                        });
                    }
                });
            });
            form.elements.radiusMeters.addEventListener('input', function () {
                if (radiusCircle) {
                    radiusCircle.setRadius(numberFieldValue('radiusMeters'));
                    updateRadiusHandle();
                }
            });

            form.addEventListener('submit', function (event) {
                event.preventDefault();

                const mode = modeSelect.value;
                const modeLabel = selectedModeLabel();
                const submitButton = form.querySelector('button[type="submit"]');
                if (submitButton) {
                    submitButton.disabled = true;
                }
                if (isMapTabActive()) {
                    initializeMap();
                }

                const request = searchRequest(mode);
                fetch(request.url, request.options)
                    .then((response) => {
                        if (!response.ok) {
                            throw new Error('Clinic search failed');
                        }
                        return response.json();
                    })
                    .then((clinics) => {
                        updateSummary(modeLabel, clinics.length);
                        renderClinics(clinics, modeLabel);
                    })
                    .catch(() => {
                        updateSummary(modeLabel, 0);
                        renderEmptyState();
                    })
                    .finally(() => {
                        if (submitButton) {
                            submitButton.disabled = false;
                        }
                    });
            });

            applyMode(modeSelect.value);
        })();
    </script>
`, msg = msg, request = request, locale = locale)
