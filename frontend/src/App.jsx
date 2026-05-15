import { Fragment, useEffect, useState } from 'react'
import './App.css'

const API_URL = 'http://localhost:8080'
const EVENT_TYPES = [
  'HR_INTERVIEW',
  'TECH_INTERVIEW',
  'FINAL_INTERVIEW',
  'TASK',
  'REJECTED',
  'OFFER',
]

const emptyApplicationForm = {
  company: '',
  role: '',
  notes: '',
}

const emptyEventForm = {
  type: 'HR_INTERVIEW',
  eventDate: getToday(),
  notes: '',
}

function App() {
  const [activePage, setActivePage] = useState('dashboard')

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <span className="brand-mark">J</span>
          <div>
            <strong>Job Tracker</strong>
            <span>Dashboard</span>
          </div>
        </div>

        <nav className="nav-tabs">
          <button
            className={activePage === 'dashboard' ? 'active' : ''}
            onClick={() => setActivePage('dashboard')}
          >
            Dashboard
          </button>
          <button
            className={activePage === 'applications' ? 'active' : ''}
            onClick={() => setActivePage('applications')}
          >
            Applications
          </button>
        </nav>
      </aside>

      <main className="content">
        {activePage === 'dashboard' ? <Dashboard /> : <Applications />}
      </main>
    </div>
  )
}

function Dashboard() {
  const currentYear = new Date().getFullYear()
  const [statistics, setStatistics] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadStatistics() {
      try {
        setLoading(true)
        setError('')

        const response = await fetch(`${API_URL}/statistics/year/${currentYear}`)

        if (!response.ok) {
          throw new Error('Could not load statistics')
        }

        setStatistics(await response.json())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    loadStatistics()
  }, [currentYear])

  const cards = [
    { label: 'Applications', value: statistics?.applications ?? 0 },
    { label: 'Interviews', value: statistics?.interviews ?? 0 },
    { label: 'Offers', value: statistics?.offers ?? 0 },
    { label: 'Rejections', value: statistics?.rejections ?? 0 },
  ]

  return (
    <section>
      <PageHeader
        title="Dashboard"
        description={`Overview for ${currentYear}`}
      />

      {loading && <StateMessage message="Loading statistics..." />}
      {error && <StateMessage message={error} type="error" />}

      {!loading && !error && (
        <>
          <div className="stats-grid">
            {cards.map((card) => (
              <article className="stat-card" key={card.label}>
                <span>{card.label}</span>
                <strong>{card.value}</strong>
              </article>
            ))}
          </div>

          <div className="chart-card">
            <div className="card-heading">
              <h2>Year summary</h2>
              <span>{currentYear}</span>
            </div>
            <div className="bar-chart">
              {cards.map((card) => (
                <ChartBar
                  key={card.label}
                  label={card.label}
                  value={card.value}
                  max={Math.max(...cards.map((item) => item.value), 1)}
                />
              ))}
            </div>
          </div>
        </>
      )}
    </section>
  )
}

function Applications() {
  const [applications, setApplications] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showApplicationForm, setShowApplicationForm] = useState(false)
  const [applicationForm, setApplicationForm] = useState(emptyApplicationForm)
  const [savingApplication, setSavingApplication] = useState(false)
  const [applicationError, setApplicationError] = useState('')
  const [openEventFormId, setOpenEventFormId] = useState(null)
  const [eventForm, setEventForm] = useState(emptyEventForm)
  const [savingEventId, setSavingEventId] = useState(null)
  const [eventError, setEventError] = useState('')

  useEffect(() => {
    async function loadApplications() {
      try {
        setLoading(true)
        setError('')
        setApplications(await fetchApplications())
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    loadApplications()
  }, [])

  async function refreshApplications() {
    setApplications(await fetchApplications())
  }

  function openAddApplicationForm() {
    setApplicationForm(emptyApplicationForm)
    setApplicationError('')
    setShowApplicationForm(true)
  }

  function closeAddApplicationForm() {
    if (!savingApplication) {
      setShowApplicationForm(false)
    }
  }

  async function handleCreateApplication(event) {
    event.preventDefault()

    try {
      setSavingApplication(true)
      setApplicationError('')

      const response = await fetch(`${API_URL}/applications`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          company: applicationForm.company.trim(),
          role: applicationForm.role.trim(),
          notes: applicationForm.notes.trim(),
        }),
      })

      if (!response.ok) {
        throw new Error('Could not create application')
      }

      const createdApplication = await response.json()
      const eventResponse = await fetch(`${API_URL}/applications/${createdApplication.id}/events`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          type: 'APPLIED',
          eventDate: getToday(),
          notes: 'Application created',
        }),
      })

      if (!eventResponse.ok) {
        throw new Error('Application was created, but the first event could not be saved')
      }

      setShowApplicationForm(false)
      setApplicationForm(emptyApplicationForm)
      await refreshApplications()
    } catch (err) {
      setApplicationError(err.message)
    } finally {
      setSavingApplication(false)
    }
  }

  function openAddEventForm(applicationId) {
    setOpenEventFormId(applicationId)
    setEventForm(emptyEventForm)
    setEventError('')
  }

  async function handleCreateEvent(event, applicationId) {
    event.preventDefault()

    try {
      setSavingEventId(applicationId)
      setEventError('')

      const response = await fetch(`${API_URL}/applications/${applicationId}/events`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          type: eventForm.type,
          eventDate: eventForm.eventDate,
          notes: eventForm.notes.trim(),
        }),
      })

      if (!response.ok) {
        throw new Error('Could not add event')
      }

      setOpenEventFormId(null)
      setEventForm(emptyEventForm)
      await refreshApplications()
    } catch (err) {
      setEventError(err.message)
    } finally {
      setSavingEventId(null)
    }
  }

  return (
    <section>
      <PageHeader
        title="Applications"
        description="All tracked job applications"
      >
        <button className="primary-button" onClick={openAddApplicationForm}>
          Add New Application
        </button>
      </PageHeader>

      {loading && <StateMessage message="Loading applications..." />}
      {error && <StateMessage message={error} type="error" />}

      {!loading && !error && (
        <div className="table-card">
          {applications.length === 0 ? (
            <StateMessage message="No applications yet." />
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Company</th>
                  <th>Role</th>
                  <th>Current status</th>
                  <th>Latest event date</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((application) => {
                  const latestEvent = getLatestEvent(application.events)
                  const rowKey = application.id ?? `${application.company}-${application.role}`

                  return (
                    <Fragment key={rowKey}>
                      <tr>
                        <td>{application.company}</td>
                        <td>{application.role}</td>
                        <td>
                          <span className="status-pill">
                            {formatStatus(latestEvent?.type)}
                          </span>
                        </td>
                        <td>{formatDate(latestEvent?.eventDate)}</td>
                        <td>
                          <button
                            className="secondary-button"
                            onClick={() => openAddEventForm(application.id)}
                          >
                            Add event
                          </button>
                        </td>
                      </tr>
                      {openEventFormId === application.id && (
                        <tr key={`${rowKey}-event-form`}>
                          <td colSpan="5">
                            <form
                              className="inline-form"
                              onSubmit={(event) => handleCreateEvent(event, application.id)}
                            >
                              <label>
                                Event type
                                <select
                                  value={eventForm.type}
                                  onChange={(event) =>
                                    setEventForm({ ...eventForm, type: event.target.value })
                                  }
                                >
                                  {EVENT_TYPES.map((type) => (
                                    <option value={type} key={type}>
                                      {formatStatus(type)}
                                    </option>
                                  ))}
                                </select>
                              </label>

                              <label>
                                Event date
                                <input
                                  type="date"
                                  value={eventForm.eventDate}
                                  onChange={(event) =>
                                    setEventForm({ ...eventForm, eventDate: event.target.value })
                                  }
                                />
                              </label>

                              <label className="wide-field">
                                Notes
                                <input
                                  value={eventForm.notes}
                                  onChange={(event) =>
                                    setEventForm({ ...eventForm, notes: event.target.value })
                                  }
                                  placeholder="Short note"
                                />
                              </label>

                              <div className="form-actions">
                                <button
                                  className="primary-button"
                                  type="submit"
                                  disabled={savingEventId === application.id}
                                >
                                  {savingEventId === application.id ? 'Saving...' : 'Save event'}
                                </button>
                                <button
                                  className="text-button"
                                  type="button"
                                  onClick={() => setOpenEventFormId(null)}
                                  disabled={savingEventId === application.id}
                                >
                                  Cancel
                                </button>
                              </div>

                              {eventError && <StateMessage message={eventError} type="error" />}
                            </form>
                          </td>
                        </tr>
                      )}
                    </Fragment>
                  )
                })}
              </tbody>
            </table>
          )}
        </div>
      )}

      {showApplicationForm && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <div className="card-heading">
              <h2>Add application</h2>
              <button
                className="icon-button"
                onClick={closeAddApplicationForm}
                type="button"
                aria-label="Close"
              >
                x
              </button>
            </div>

            <form className="modal-form" onSubmit={handleCreateApplication}>
              <label>
                Company
                <input
                  required
                  value={applicationForm.company}
                  onChange={(event) =>
                    setApplicationForm({ ...applicationForm, company: event.target.value })
                  }
                />
              </label>

              <label>
                Role
                <input
                  required
                  value={applicationForm.role}
                  onChange={(event) =>
                    setApplicationForm({ ...applicationForm, role: event.target.value })
                  }
                />
              </label>

              <label>
                Note
                <textarea
                  rows="4"
                  value={applicationForm.notes}
                  onChange={(event) =>
                    setApplicationForm({ ...applicationForm, notes: event.target.value })
                  }
                />
              </label>

              {applicationError && <StateMessage message={applicationError} type="error" />}

              <div className="form-actions">
                <button className="primary-button" type="submit" disabled={savingApplication}>
                  {savingApplication ? 'Saving...' : 'Create application'}
                </button>
                <button
                  className="text-button"
                  type="button"
                  onClick={closeAddApplicationForm}
                  disabled={savingApplication}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  )
}

function PageHeader({ title, description, children }) {
  return (
    <header className="page-header">
      <div>
        <h1>{title}</h1>
        <p>{description}</p>
      </div>
      {children}
    </header>
  )
}

function StateMessage({ message, type = 'info' }) {
  return <div className={`state-message ${type}`}>{message}</div>
}

function ChartBar({ label, value, max }) {
  return (
    <div className="chart-row">
      <span>{label}</span>
      <div className="bar-track">
        <div
          className="bar-fill"
          style={{ width: `${Math.max((value / max) * 100, 3)}%` }}
        />
      </div>
      <strong>{value}</strong>
    </div>
  )
}

function getLatestEvent(events = []) {
  if (!events.length) {
    return null
  }

  return [...events].sort((a, b) => {
    const dateA = a.eventDate ? new Date(a.eventDate).getTime() : 0
    const dateB = b.eventDate ? new Date(b.eventDate).getTime() : 0

    if (dateA !== dateB) {
      return dateB - dateA
    }

    return (b.id ?? 0) - (a.id ?? 0)
  })[0]
}

async function fetchApplications() {
  const response = await fetch(`${API_URL}/applications`)

  if (!response.ok) {
    throw new Error('Could not load applications')
  }

  return response.json()
}

function formatStatus(status) {
  if (!status) {
    return 'No events'
  }

  return status
    .toLowerCase()
    .split('_')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ')
}

function formatDate(date) {
  if (!date) {
    return '-'
  }

  return new Intl.DateTimeFormat('en', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  }).format(new Date(date))
}

function getToday() {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')

  return `${year}-${month}-${day}`
}

export default App
