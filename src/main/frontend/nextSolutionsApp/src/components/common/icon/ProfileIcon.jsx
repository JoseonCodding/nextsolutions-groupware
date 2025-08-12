const ProfileIcon = ({ color = 'fill-gray-300', size = '100%' }) => {
  return (
    <span>
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 600 600"
        width={size}
        height={size}
        className={color}
      >
        <circle cx="300" cy="300" r="300" />
        <path
          d="M124.25,477.728C152.711,408.665 220.717,360 300,360C379.283,360 447.289,408.665 475.75,477.728C430.578,522.401 368.487,550 300,550C231.513,550 169.422,522.401 124.25,477.728Z"
          className="fill-white"
        />
        <circle cx="300" cy="230" r="100" className="fill-white" />
      </svg>
    </span>
  );
};

export default ProfileIcon;
