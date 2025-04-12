import { useState, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
    const [count, setCount] = useState(0)
    const [message, setMessage] = useState('')
    const [users, setUsers] = useState([])
    const [newUser, setNewUser] = useState({ firstname: '', lastname: '', age: '' })
    const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080';
// Then replace all fetch calls with ${apiUrl} instead of http://localhost:8080


    useEffect(() => {
        // Fetch hello message
        fetch(`${apiUrl}/hello`)
            .then(response => response.json())
            .then(data => setMessage(data.message))
            .catch(error => console.error('Error fetching hello:', error))

        // Fetch users
        fetchUsers()
    }, [])

    const fetchUsers = () => {
        fetch(`${apiUrl}/users`)
            .then(response => response.json())
            .then(data => setUsers(data))
            .catch(error => console.error('Error fetching users:', error))
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        fetch(`${apiUrl}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                firstname: newUser.firstname,
                lastname: newUser.lastname,
                age: parseInt(newUser.age)
            })
        })
            .then(response => response.json())
            .then(() => {
                fetchUsers()
                setNewUser({ firstname: '', lastname: '', age: '' })
            })
            .catch(error => console.error('Error creating user:', error))
    }

    const handleInputChange = (e) => {
        setNewUser({...newUser, [e.target.name]: e.target.value})
    }

    return (
        <>
            <div>
                <a href="https://vite.dev" target="_blank">
                    <img src={viteLogo} className="logo" alt="Vite logo" />
                </a>
                <a href="https://react.dev" target="_blank">
                    <img src={reactLogo} className="logo react" alt="React logo" />
                </a>
            </div>
            <h1>Vite + React</h1>

            {/* Display hello message */}
            <div className="card">
                <h2>Backend Message: {message}</h2>
            </div>

            {/* Add user form */}
            <div className="card">
                <h3>Add New User</h3>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="firstname"
                        placeholder="First Name"
                        value={newUser.firstname}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="text"
                        name="lastname"
                        placeholder="Last Name"
                        value={newUser.lastname}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        type="number"
                        name="age"
                        placeholder="Age"
                        value={newUser.age}
                        onChange={handleInputChange}
                        required
                    />
                    <button type="submit">Add User</button>
                </form>
            </div>

            {/* Display users */}
            <div className="card">
                <h3>Users</h3>
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Age</th>
                    </tr>
                    </thead>
                    <tbody>
                    {users.map(user => (
                        <tr key={user.id}>
                            <td>{user.id}</td>
                            <td>{user.firstname}</td>
                            <td>{user.lastname}</td>
                            <td>{user.age}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <div className="card">
                <button onClick={() => setCount((count) => count + 1)}>
                    count is {count}
                </button>
                <p>
                    Edit <code>src/App.jsx</code> and save to test HMR
                </p>
            </div>
        </>
    )
}

export default App