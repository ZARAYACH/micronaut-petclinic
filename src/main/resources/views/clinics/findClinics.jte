@import io.micronaut.samples.petclinic.dto.ClinicDto
@import java.util.List

@param List<ClinicDto> clinics
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

        .clinic-map-instructions {
            color: #6c757d;
            font-size: 0.875rem;
            line-height: 1.4;
            margin-top: 0.5rem;
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
                            <option value="near" selected>${msg.text("clinic.mode.near")}</option>
                            <option value="within">${msg.text("clinic.mode.within")}</option>
                            <option value="intersects">${msg.text("clinic.mode.intersects")}</option>
                        </select>
                    </div>
                </div>

                <ul class="nav nav-tabs mb-3" id="clinic-search-tabs" role="tablist">
                    <li class="nav-item" role="presentation">
                        <button class="nav-link active" id="map-search-tab" type="button" role="tab"
                                data-bs-toggle="tab" data-bs-target="#map-search-panel"
                                aria-controls="map-search-panel" aria-selected="true">
                            <i class="fas fa-map me-2"></i>${msg.text("clinic.searchTab.map")}
                        </button>
                    </li>
                    <li class="nav-item" role="presentation">
                        <button class="nav-link" id="manual-search-tab" type="button" role="tab"
                                data-bs-toggle="tab" data-bs-target="#manual-search-panel"
                                aria-controls="manual-search-panel" aria-selected="false">
                            <i class="fas fa-keyboard me-2"></i>${msg.text("clinic.searchTab.manual")}
                        </button>
                    </li>
                </ul>

                <div class="tab-content">
                    <div class="tab-pane fade" id="manual-search-panel" role="tabpanel"
                         aria-labelledby="manual-search-tab">
                        <div class="row g-3">
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="latitude" class="form-label">
                                    <i class="fas fa-map-pin me-2"></i>${msg.text("clinic.latitude")}
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="latitude"
                                       name="latitude" value="43.0731">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="longitude" class="form-label">
                                    <i class="fas fa-map-pin me-2"></i>${msg.text("clinic.longitude")}
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="longitude"
                                       name="longitude" value="-89.4012">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-near">
                                <label for="radiusMeters" class="form-label">
                                    <i class="fas fa-ruler-combined me-2"></i>${msg.text("clinic.radiusMeters")}
                                </label>
                                <input type="number" step="100" min="100" class="form-control form-control-lg" id="radiusMeters"
                                       name="radiusMeters" value="5000">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="minLatitude" class="form-label">
                                    <span id="minLatitudeLabel"
                                          data-within-label="${msg.text("clinic.minLatitude")}"
                                          data-intersects-label="${msg.text("clinic.startLatitude")}">${msg.text("clinic.minLatitude")}</span>
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="minLatitude"
                                       name="minLatitude" value="43.0753">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="minLongitude" class="form-label">
                                    <span id="minLongitudeLabel"
                                          data-within-label="${msg.text("clinic.minLongitude")}"
                                          data-intersects-label="${msg.text("clinic.startLongitude")}">${msg.text("clinic.minLongitude")}</span>
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="minLongitude"
                                       name="minLongitude" value="-89.5186">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="maxLatitude" class="form-label">
                                    <span id="maxLatitudeLabel"
                                          data-within-label="${msg.text("clinic.maxLatitude")}"
                                          data-intersects-label="${msg.text("clinic.endLatitude")}">${msg.text("clinic.maxLatitude")}</span>
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="maxLatitude"
                                       name="maxLatitude" value="43.1836">
                            </div>
                            <div class="col-md-4 clinic-field clinic-field-bounds">
                                <label for="maxLongitude" class="form-label">
                                    <span id="maxLongitudeLabel"
                                          data-within-label="${msg.text("clinic.maxLongitude")}"
                                          data-intersects-label="${msg.text("clinic.endLongitude")}">${msg.text("clinic.maxLongitude")}</span>
                                </label>
                                <input type="number" step="0.0001" class="form-control form-control-lg" id="maxLongitude"
                                       name="maxLongitude" value="-89.2137">
                            </div>
                            <div class="col-md-4 d-flex align-items-end gap-2">
                                <button class="btn btn-secondary btn-lg w-50 clinic-reset-search" type="button">
                                    <i class="fas fa-rotate-left me-2"></i>Reset
                                </button>
                                <button class="btn btn-primary btn-lg w-50" type="submit">
                                    <i class="fas fa-search me-2"></i>${msg.text("button.search")}
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="tab-pane fade show active" id="map-search-panel" role="tabpanel" aria-labelledby="map-search-tab">
                        <div id="clinic-map" class="clinic-map"></div>
                        <div class="clinic-map-instructions" id="clinic-map-instructions" role="status"
                             data-near-instruction="${msg.text("clinic.mapInstruction.near")}"
                             data-within-instruction="${msg.text("clinic.mapInstruction.within")}"
                             data-intersects-instruction="${msg.text("clinic.mapInstruction.intersects")}"
                             data-near-required="${msg.text("clinic.mapValidation.nearRequired")}"
                             data-within-required="${msg.text("clinic.mapValidation.withinRequired")}"
                             data-within-self-intersection="${msg.text("clinic.mapValidation.withinSelfIntersection")}"
                             data-within-self-intersection-move="${msg.text("clinic.mapValidation.withinSelfIntersectionMove")}"
                             data-intersects-required="${msg.text("clinic.mapValidation.intersectsRequired")}">
                            ${msg.text("clinic.mapInstruction.near")}
                        </div>
                        <div class="mt-3 d-flex justify-content-end gap-2">
                            <button class="btn btn-outline-secondary btn-lg d-none" type="button"
                                    id="clinic-example-line">
                                <i class="fas fa-route me-2"></i>${msg.text("clinic.exampleLine")}
                            </button>
                            <button class="btn btn-secondary btn-lg clinic-reset-search" type="button">
                                <i class="fas fa-rotate-left me-2"></i>Reset
                            </button>
                            <button class="btn btn-primary btn-lg" type="submit">
                                <i class="fas fa-search me-2"></i>${msg.text("button.search")}
                            </button>
                        </div>
                    </div>
                </div>
            </form>
        </div>

        <section id="clinic-results-section" class="d-none">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <h2 class="mb-1" id="clinic-results-title"
                        data-template="${msg.text("clinic.resultsTitle", "__mode__")}">${msg.text("clinic.resultsTitle", msg.text("clinic.mode.near"))}</h2>
                    <p class="text-muted mb-0" id="clinic-results-count"
                       data-template="${msg.text("clinic.resultsCount", "__count__")}">${msg.text("clinic.resultsCount", 0)}</p>
                </div>
            </div>

            <div id="clinic-results"
                 data-empty-title="${msg.text("clinic.noClinicsFound")}"
                 data-empty-message="${msg.text("clinic.noClinicsMessage")}"
                 data-coordinates-label="${msg.text("clinic.coordinates")}">
            </div>
        </section>
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
            const mapInstructions = document.getElementById('clinic-map-instructions');
            const exampleLineButton = document.getElementById('clinic-example-line');
            const results = document.getElementById('clinic-results');
            const resultsSection = document.getElementById('clinic-results-section');
            const resultsTitle = document.getElementById('clinic-results-title');
            const resultsCount = document.getElementById('clinic-results-count');
            if (!form || !modeSelect || !results || !resultsSection || !resultsTitle || !resultsCount) {
                return;
            }

            const nearFields = document.querySelectorAll('.clinic-field-near');
            const boundsFields = document.querySelectorAll('.clinic-field-bounds');
            const manualCoordinateLabels = [
                document.getElementById('minLatitudeLabel'),
                document.getElementById('minLongitudeLabel'),
                document.getElementById('maxLatitudeLabel'),
                document.getElementById('maxLongitudeLabel')
            ];
            const mapBounds = [[42.88, -89.75], [43.27, -89.12]];
            const mapCenter = [43.0731, -89.4012];
            const mapZoom = 10;
            const exampleIntersectingLine = [
                [43.0753, -89.5186],
                [43.1020, -89.3545],
                [43.1836, -89.2137]
            ];
            let clinicMap = null;
            let referenceMarker = null;
            let radiusCircle = null;
            let radiusHandle = null;
            let hasNearSelection = false;
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
                updateManualCoordinateLabels(mode);
                resetMapAndResults();
                toggleExampleLineButton(mode);
                updateMapInstructions(mode);
                applyMapMode(mode);
            }

            function updateManualCoordinateLabels(mode) {
                const labelKey = mode === 'intersects' ? 'intersectsLabel' : 'withinLabel';
                manualCoordinateLabels.forEach(function (label) {
                    if (label && label.dataset[labelKey]) {
                        label.textContent = label.dataset[labelKey];
                    }
                });
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

            function searchRequest(mode) {
                if (mode === 'near') {
                    return {
                        url: '/clinics/nearby',
                        options: {
                            method: 'POST',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify({
                                latitude: numberFieldValue('latitude'),
                                longitude: numberFieldValue('longitude'),
                                radiusMeters: numberFieldValue('radiusMeters')
                            })
                        }
                    };
                }
                const coordinates = isMapTabActive() && drawnLayer
                    ? drawnLayerCoordinates()
                    : manualCoordinates(mode);
                return {
                    url: '/clinics/' + mode,
                    options: {
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            coordinates: coordinates
                        })
                    }
                };
            }

            function manualCoordinates(mode) {
                if (mode === 'intersects') {
                    return manualLineCoordinates();
                }
                return boundingBoxCoordinates();
            }

            function manualLineCoordinates() {
                return [
                    {latitude: numberFieldValue('minLatitude'), longitude: numberFieldValue('minLongitude')},
                    {latitude: numberFieldValue('maxLatitude'), longitude: numberFieldValue('maxLongitude')}
                ];
            }

            function boundingBoxCoordinates() {
                const minLatitude = numberFieldValue('minLatitude');
                const minLongitude = numberFieldValue('minLongitude');
                const maxLatitude = numberFieldValue('maxLatitude');
                const maxLongitude = numberFieldValue('maxLongitude');
                const coordinates = [
                    {latitude: minLatitude, longitude: minLongitude},
                    {latitude: maxLatitude, longitude: minLongitude},
                    {latitude: maxLatitude, longitude: maxLongitude},
                    {latitude: minLatitude, longitude: maxLongitude},
                    {latitude: minLatitude, longitude: minLongitude}
                ];
                return coordinates;
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

            function showResultsSection() {
                resultsSection.classList.remove('d-none');
            }

            function hideResultsSection() {
                resultsSection.classList.add('d-none');
                results.innerHTML = '';
                renderMapMarkers([]);
                updateSummary(selectedModeLabel(), 0);
            }

            function renderEmptyState() {
                showResultsSection();
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
                showResultsSection();
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
                    minZoom: mapZoom
                }).setView(mapCenter, mapZoom);
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
                });
                radiusCircle = L.circle(referenceMarker.getLatLng(), {
                    radius: numberFieldValue('radiusMeters'),
                    color: '#0d6efd',
                    fillColor: '#0d6efd',
                    fillOpacity: 0.12
                });
                radiusHandle = L.marker(radiusHandleLatLng(), {
                    draggable: true,
                    title: 'Drag to resize radius'
                });

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
                    if (hasNearSelection) {
                        showNearSelection();
                        updateRadiusHandle();
                    } else {
                        hideNearSelection();
                    }
                } else {
                    hideNearSelection();
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
                if (mode !== 'near' && typeof clinicMap.pm.setGlobalOptions === 'function') {
                    clinicMap.pm.setGlobalOptions({
                        allowSelfIntersection: false
                    });
                }
            }

            function setNearPoint(latlng) {
                const normalized = clinicMap.wrapLatLng(L.latLng(latlng.lat, latlng.lng));
                hasNearSelection = true;
                referenceMarker.setLatLng(normalized);
                radiusCircle.setLatLng(normalized);
                radiusCircle.setRadius(numberFieldValue('radiusMeters'));
                setFieldValue('latitude', normalized.lat.toFixed(4));
                setFieldValue('longitude', normalized.lng.toFixed(4));
                showNearSelection();
                updateRadiusHandle();
            }

            function showNearSelection() {
                if (!clinicMap.hasLayer(referenceMarker)) {
                    referenceMarker.addTo(clinicMap);
                }
                if (!clinicMap.hasLayer(radiusCircle)) {
                    radiusCircle.addTo(clinicMap);
                }
                if (!clinicMap.hasLayer(radiusHandle)) {
                    radiusHandle.addTo(clinicMap);
                }
            }

            function hideNearSelection() {
                if (!clinicMap) {
                    return;
                }
                if (referenceMarker && clinicMap.hasLayer(referenceMarker)) {
                    clinicMap.removeLayer(referenceMarker);
                }
                if (radiusCircle && clinicMap.hasLayer(radiusCircle)) {
                    clinicMap.removeLayer(radiusCircle);
                }
                if (radiusHandle && clinicMap.hasLayer(radiusHandle)) {
                    clinicMap.removeLayer(radiusHandle);
                }
            }

            function clearNearSelection() {
                hasNearSelection = false;
                hideNearSelection();
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
                }
                const bounds = drawnLayer.getBounds();
                setFieldValue('minLatitude', bounds.getSouth().toFixed(4));
                setFieldValue('minLongitude', bounds.getWest().toFixed(4));
                setFieldValue('maxLatitude', bounds.getNorth().toFixed(4));
                setFieldValue('maxLongitude', bounds.getEast().toFixed(4));
            }

            function appendDrawPoint(latlng) {
                if (drawnPoints.length === 0 && drawnLayer && clinicMap.hasLayer(drawnLayer)) {
                    clinicMap.removeLayer(drawnLayer);
                    drawnLayer = null;
                }
                const nextPoints = drawnPoints.concat([L.latLng(latlng.lat, latlng.lng)]);
                if (modeSelect.value === 'within' && isSelfIntersectingPolygon(nextPoints)) {
                    showMapInstructionError(mapInstructionText('withinSelfIntersection'));
                    return;
                }
                drawnPoints = nextPoints;
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
                updateDrawingValidation();
            }

            function enableDrawnLayerEditing() {
                if (drawnLayer && drawnLayer.pm) {
                    drawnLayer.pm.enable();
                    drawnLayer.on('pm:edit', syncDrawnLayer);
                    drawnLayer.on('pm:update', syncDrawnLayer);
                }
            }

            function linePointsForMode() {
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
                        updateDrawingValidation();
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

            function resetMapAndResults() {
                clearDrawing();
                clearNearSelection();
                hideResultsSection();
                if (clinicMap) {
                    clinicMap.setView(mapCenter, mapZoom);
                }
            }

            function toggleExampleLineButton(mode) {
                if (exampleLineButton) {
                    exampleLineButton.classList.toggle('d-none', mode !== 'intersects');
                }
            }

            function useExampleIntersectingLine() {
                initializeMap();
                if (!clinicMap) {
                    return;
                }
                modeSelect.value = 'intersects';
                toggleGroup(nearFields, true);
                toggleGroup(boundsFields, false);
                toggleExampleLineButton('intersects');
                clearDrawing();
                clearNearSelection();
                hideResultsSection();
                drawnPoints = exampleIntersectingLine.map(function (point) {
                    return L.latLng(point[0], point[1]);
                });
                renderDraftPointMarkers();
                renderDrawnLayerFromPoints();
                if (drawnLayer) {
                    clinicMap.fitBounds(drawnLayer.getBounds().pad(0.3), {
                        maxZoom: 11
                    });
                }
                updateMapInstructions('intersects');
                runClinicSearch();
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
                const points = modeSelect.value === 'intersects'
                    ? linePointsForMode()
                    : closedPointsForMode();
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

            function isSelfIntersectingPolygon(points) {
                const shell = removeClosingPoint(points);
                if (shell.length < 4) {
                    return false;
                }
                for (let i = 0; i < shell.length; i++) {
                    const firstStart = shell[i];
                    const firstEnd = shell[(i + 1) % shell.length];
                    for (let j = i + 1; j < shell.length; j++) {
                        if (areAdjacentSegments(i, j, shell.length)) {
                            continue;
                        }
                        const secondStart = shell[j];
                        const secondEnd = shell[(j + 1) % shell.length];
                        if (segmentsIntersect(firstStart, firstEnd, secondStart, secondEnd)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            function areAdjacentSegments(firstIndex, secondIndex, segmentCount) {
                return Math.abs(firstIndex - secondIndex) === 1
                    || (firstIndex === 0 && secondIndex === segmentCount - 1);
            }

            function segmentsIntersect(firstStart, firstEnd, secondStart, secondEnd) {
                const d1 = direction(firstStart, firstEnd, secondStart);
                const d2 = direction(firstStart, firstEnd, secondEnd);
                const d3 = direction(secondStart, secondEnd, firstStart);
                const d4 = direction(secondStart, secondEnd, firstEnd);
                if (((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0))
                    && ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))) {
                    return true;
                }
                return d1 === 0 && isPointOnSegment(secondStart, firstStart, firstEnd)
                    || d2 === 0 && isPointOnSegment(secondEnd, firstStart, firstEnd)
                    || d3 === 0 && isPointOnSegment(firstStart, secondStart, secondEnd)
                    || d4 === 0 && isPointOnSegment(firstEnd, secondStart, secondEnd);
            }

            function direction(start, end, point) {
                return (end.lng - start.lng) * (point.lat - start.lat)
                    - (end.lat - start.lat) * (point.lng - start.lng);
            }

            function isPointOnSegment(point, start, end) {
                return point.lng >= Math.min(start.lng, end.lng)
                    && point.lng <= Math.max(start.lng, end.lng)
                    && point.lat >= Math.min(start.lat, end.lat)
                    && point.lat <= Math.max(start.lat, end.lat);
            }

            function updateDrawingValidation() {
                if (modeSelect.value === 'within' && isSelfIntersectingPolygon(drawnPoints)) {
                    showMapInstructionError(mapInstructionText('withinSelfIntersectionMove'));
                } else {
                    updateMapInstructions(modeSelect.value);
                }
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
                if (isMapTabActive()) {
                    initializeMap();
                }
            });
            if (mapTab) {
                mapTab.addEventListener('click', function () {
                    setTimeout(initializeMap, 150);
                });
            }
            document.querySelectorAll('.clinic-reset-search').forEach(function (button) {
                button.addEventListener('click', resetMapAndResults);
            });
            if (exampleLineButton) {
                exampleLineButton.addEventListener('click', useExampleIntersectingLine);
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

            function updateMapInstructions(mode) {
                if (!mapInstructions) {
                    return;
                }
                mapInstructions.classList.remove('text-danger');
                if (mode === 'near') {
                    mapInstructions.textContent = mapInstructionText('nearInstruction');
                } else if (mode === 'within') {
                    mapInstructions.textContent = mapInstructionText('withinInstruction');
                } else {
                    mapInstructions.textContent = mapInstructionText('intersectsInstruction');
                }
            }

            function mapInstructionText(name) {
                return mapInstructions && mapInstructions.dataset[name] ? mapInstructions.dataset[name] : '';
            }

            function showMapInstructionError(message) {
                if (!mapInstructions) {
                    return;
                }
                mapInstructions.classList.add('text-danger');
                mapInstructions.textContent = message;
            }

            function mapSearchValidationMessage(mode) {
                if (mode === 'near') {
                    return hasNearSelection ? null : mapInstructionText('nearRequired');
                }
                if (mode === 'within') {
                    if (!drawnLayer || drawnPoints.length < 3) {
                        return mapInstructionText('withinRequired');
                    }
                    if (isSelfIntersectingPolygon(drawnPoints)) {
                        return mapInstructionText('withinSelfIntersection');
                    }
                    return null;
                }
                return drawnLayer && drawnPoints.length >= 2 ? null : mapInstructionText('intersectsRequired');
            }

            function activateMapTab() {
                if (!mapTab || !mapPanel) {
                    return;
                }
                if (typeof bootstrap !== 'undefined' && bootstrap.Tab) {
                    bootstrap.Tab.getOrCreateInstance(mapTab).show();
                } else {
                    const manualTab = document.getElementById('manual-search-tab');
                    const manualPanel = document.getElementById('manual-search-panel');
                    if (manualTab && manualPanel) {
                        manualTab.classList.remove('active');
                        manualTab.setAttribute('aria-selected', 'false');
                        manualPanel.classList.remove('show', 'active');
                    }
                    mapTab.classList.add('active');
                    mapTab.setAttribute('aria-selected', 'true');
                    mapPanel.classList.add('show', 'active');
                }
                setTimeout(initializeMap, 150);
            }

            form.addEventListener('submit', function (event) {
                event.preventDefault();
                runClinicSearch();
            });

            function runClinicSearch() {
                const mode = modeSelect.value;
                const modeLabel = selectedModeLabel();
                const submitButton = form.querySelector('button[type="submit"]');
                if (submitButton) {
                    submitButton.disabled = true;
                }
                if (isMapTabActive()) {
                    initializeMap();
                }
                if (isMapTabActive()) {
                    const validationMessage = mapSearchValidationMessage(mode);
                    if (validationMessage) {
                        showMapInstructionError(validationMessage);
                        hideResultsSection();
                        if (submitButton) {
                            submitButton.disabled = false;
                        }
                        return;
                    }
                    updateMapInstructions(mode);
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
            }

            applyMode(modeSelect.value);
            if (isMapTabActive()) {
                initializeMap();
            }
        })();
    </script>
`, msg = msg, request = request, locale = locale)
