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
    <div class="page-header">
        <div class="container">
            <h1><i class="fas fa-location-dot me-3"></i>${msg.text("clinic.findNearby")}</h1>
            <p>${msg.text("clinic.spatialIntro")}</p>
        </div>
    </div>

    <main class="container flex-grow-1 py-4">
        <div class="search-box mb-4 animate-fade-in-up">
            <form action="/clinics" method="get" id="clinic-search-form">
                <div class="row g-3">
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

    <script>
        (function () {
            const form = document.getElementById('clinic-search-form');
            const modeSelect = document.getElementById('mode');
            const results = document.getElementById('clinic-results');
            const resultsTitle = document.getElementById('clinic-results-title');
            const resultsCount = document.getElementById('clinic-results-count');
            if (!form || !modeSelect || !results || !resultsTitle || !resultsCount) {
                return;
            }

            const nearFields = document.querySelectorAll('.clinic-field-near');
            const boundsFields = document.querySelectorAll('.clinic-field-bounds');

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
            }

            function fieldValue(name) {
                return form.elements[name].value;
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
            }

            modeSelect.addEventListener('change', function () {
                applyMode(this.value);
            });

            form.addEventListener('submit', function (event) {
                event.preventDefault();

                const mode = modeSelect.value;
                const modeLabel = selectedModeLabel();
                const submitButton = form.querySelector('button[type="submit"]');
                if (submitButton) {
                    submitButton.disabled = true;
                }

                fetch(searchUrl(mode), {
                    headers: {
                        'Accept': 'application/json'
                    }
                })
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
                        form.submit();
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
